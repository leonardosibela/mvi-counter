package com.hikarisource.mvicounter

import android.app.Application
import com.hikarisource.mvicounter.core.di.KoinDependencyManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CounterApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin()
    }

    private fun startKoin() = startKoin {
        androidContext(this@CounterApplication)
        androidLogger()
        modules(KoinDependencyManager.modules)
    }
}