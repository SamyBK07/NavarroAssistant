package com.navarro.core

import android.app.Application
import android.content.Context
import com.navarro.core.AppConfig.MODELS_DIR
import java.io.File

class App : Application() {

    companion object {
        lateinit var instance: App
            private set

        fun appContext(): Context = instance.applicationContext
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        Logger.i("Application started")

        initModelDirectory()
    }

    private fun initModelDirectory() {
        val modelsDir = File(filesDir, MODELS_DIR)

        if (!modelsDir.exists()) {
            val created = modelsDir.mkdirs()
            Logger.i("Models directory created: $created")
        } else {
            Logger.i("Models directory exists")
        }
    }
}
