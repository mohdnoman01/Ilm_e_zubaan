package com.ilmezubaan.app

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.ilmezubaan.app.data.local.AppDatabase

class IlmEZubaanApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = Firebase.appCheck
        firebaseAppCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )
    }
}
