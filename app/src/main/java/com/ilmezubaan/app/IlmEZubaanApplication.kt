package com.ilmezubaan.app

import android.app.Application
import com.ilmezubaan.app.data.local.AppDatabase

class IlmEZubaanApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
