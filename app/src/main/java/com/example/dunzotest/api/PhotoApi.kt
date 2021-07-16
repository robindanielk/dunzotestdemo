package com.example.dunzotest.api

import com.example.dunzotest.model.photo.PhotoResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotoApi {

    @GET("/services/rest")
    fun getPhotos(
        @Query("text") searchTest: String,
        @Query("page") page: Int
    ): Single<PhotoResponse>
}