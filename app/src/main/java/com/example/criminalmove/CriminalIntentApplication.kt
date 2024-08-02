package com.example.criminalmove

import android.app.Application
import com.example.criminalmove.database.CrimeRepository

class CriminalIntentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}