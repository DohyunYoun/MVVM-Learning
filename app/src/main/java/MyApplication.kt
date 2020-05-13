package com.ellen.mvvmlearning;
import android.app.Application
import com.ellen.mvvmlearning.di.appModules
import org.koin.android.ext.android.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, appModules)
    }
}