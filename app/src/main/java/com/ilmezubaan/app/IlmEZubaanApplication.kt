package com.ilmezubaan.app

import android.app.Application
import com.ilmezubaan.app.data.local.AppDatabase
import dagger.hilt.android.HiltAndroidApp
import kotlin.system.exitProcess
import timber.log.Timber

@HiltAndroidApp
class IlmEZubaanApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        installCrashLogger()
    }

    private fun installCrashLogger() {
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Timber.e(throwable, "Uncaught exception in thread ${thread.name}")
            previousHandler?.uncaughtException(thread, throwable) ?: exitProcess(10)
        }
    }
}
