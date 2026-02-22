package com.navarro.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.navarro.R
import com.navarro.hotword.HotwordService
import android.content.Intent
import com.navarro.core.AppConfig
import com.navarro.core.Logger

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Logger.i("MainActivity démarrée")

        // Lancement du service Hotword au démarrage de l'activité
        val intent = Intent(this, HotwordService::class.java).apply {
            action = AppConfig.ACTION_START_HOTWORD
        }
        startForegroundService(intent)
    }
}
