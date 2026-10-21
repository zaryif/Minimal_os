package com.zaryif.minimalos

import android.app.Application

class MinimalApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: MinimalApp
            private set
    }
}
