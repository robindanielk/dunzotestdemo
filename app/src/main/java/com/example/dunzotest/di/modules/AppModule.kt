package com.example.dunzotest.di.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class AppModule {
    @Provides
    fun getContext(application: Application):Context{
        return application.applicationContext
    }
}