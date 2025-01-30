package com.example.ayush_swipe.di

import android.app.Application
import org.koin.core.context.GlobalContext.startKoin
import org.koin.android.ext.koin.androidContext


class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            modules(appModule) }
    }
}
