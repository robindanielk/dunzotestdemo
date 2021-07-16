package com.example.dunzotest.di.modules

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.example.dunzotest.BuildConfig
import com.example.dunzotest.api.PhotoApi
import com.example.dunzotest.util.AppConstants
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

const val tag = "NetworkModule"

@Module
class NetworkModule {

    @Provides
    fun getBaseUrl(): String {
        return BuildConfig.BASE_URL
    }

    @Provides
    @Named("requestInterceptor")
    fun provideRequestInterceptor(): Interceptor? {
        return Interceptor { chain ->
            var request = chain.request()
            var url = request.url()
            var newUrl = url.newBuilder()
                .addQueryParameter("api_key", BuildConfig.FLICKER_API_KEY)
                .addQueryParameter("method", "flickr.photos.search")
                .addQueryParameter("format", "json")
                .addQueryParameter("per_page", "10")
                .addQueryParameter("nojsoncallback", "1").build()
            var newRequest = request.newBuilder().url(newUrl).build()
            chain.proceed(newRequest)
        }
    }


    @Provides
    @Singleton
    fun provideOkHttpClient(
        @Named("requestInterceptor") interceptor: Interceptor?,
        @Named("cacheInterceptor") cacheInterceptor: Interceptor?,
        @Named("offlineCacheInterceptor") offlineCacheInterceptor: Interceptor?,
        cache: Cache?
    ): OkHttpClient? {
        val httpBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        httpBuilder.addInterceptor(interceptor)
        httpBuilder.addInterceptor(offlineCacheInterceptor)
        httpBuilder.addNetworkInterceptor(cacheInterceptor)
        httpBuilder.cache(cache);
        return httpBuilder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient?, baseUrl: String): Retrofit? {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    fun provideApi(retrofit: Retrofit?): PhotoApi {
        return retrofit?.create(PhotoApi::class.java)!!
    }

    @Provides
    fun provideCache(context: Context): Cache? {
        var cache: Cache? = null
        try {
            cache = Cache(File(context.getCacheDir(), "http-cache"), 10 * 1024 * 1024) // 10 MB
        } catch (e: Exception) {
            Log.e(tag, "Could not create Cache!")
        }
        return cache
    }

    @Provides
    @Named("cacheInterceptor")
    fun provideCacheInterceptor(): Interceptor? {
        return Interceptor { chain ->
            val response: Response = chain.proceed(chain.request())
            val cacheControl = CacheControl.Builder()
                .maxAge(1, TimeUnit.HOURS)
                .build()
            response.newBuilder()
                .removeHeader(AppConstants.HEADER_PRAGMA)
                .removeHeader(AppConstants.HEADER_CACHE_CONTROL)
                .header(AppConstants.HEADER_CACHE_CONTROL, cacheControl.toString())
                .build()
        }
    }

    @Provides
    @Named("offlineCacheInterceptor")
    fun provideOfflineCacheInterceptor(isConnected: Boolean): Interceptor? {
        return Interceptor { chain ->
            var request = chain.request()
            if (!isConnected) {
                val cacheControl = CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()
                request = request.newBuilder()
                    .removeHeader(AppConstants.HEADER_PRAGMA)
                    .removeHeader(AppConstants.HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build()
            }
            chain.proceed(request)
        }
    }


    @Provides
    fun isConnected(context: Context): Boolean {
        try {
            val e = context.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val activeNetwork = e.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        } catch (e: java.lang.Exception) {
            Log.e(tag, e.message)
        }
        return false
    }
}