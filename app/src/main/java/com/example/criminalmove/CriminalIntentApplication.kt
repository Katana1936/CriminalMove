package com.example.criminalmove

import android.app.Application
import com.example.criminalmove.database.CrimeDataSource

class CriminalIntentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrimeDataSource.initialize(this)
    }
}
