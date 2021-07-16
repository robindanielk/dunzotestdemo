package com.example.dunzotest.util

import android.app.Application
import com.example.dunzotest.di.DaggerMainComponent
import com.example.dunzotest.di.MainComponent

class DunzoTestApplication : Application() {
    lateinit var component: MainComponent
    override fun onCreate() {
        super.onCreate()
        component = DaggerMainComponent.builder().application(this).build()
    }

}