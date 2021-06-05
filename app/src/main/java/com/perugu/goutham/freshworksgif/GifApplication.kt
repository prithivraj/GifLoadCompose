package com.perugu.goutham.freshworksgif

import android.app.Application
import com.perugu.goutham.freshworksgif.common.commonModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GifApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GifApplication)
            modules(commonModule)
        }
    }
}