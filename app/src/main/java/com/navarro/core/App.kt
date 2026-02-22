package com.navarro.core

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialisation globale (logs, préférences, etc.)
    }
}
