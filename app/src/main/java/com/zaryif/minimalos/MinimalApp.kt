package com.zaryif.minimalos

import android.app.Application
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class MinimalApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                Log.e("MinimalOS", "CRASH in thread ${thread.name}", throwable)
                val file = File(filesDir, "crash_dump.txt")
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                file.writeText("Time: ${System.currentTimeMillis()}\nThread: ${thread.name}\n$sw\n")
            } catch (_: Throwable) {}
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    companion object {
        lateinit var instance: MinimalApp
            private set
    }
}
