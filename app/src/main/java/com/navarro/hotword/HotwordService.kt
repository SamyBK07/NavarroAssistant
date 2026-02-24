package com.navarro.hotword

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.navarro.core.AppConfig
import com.navarro.core.Logger
import org.vosk.android.StorageService

class HotwordService : Service() {

    private lateinit var hotwordRecognizer: HotwordRecognizer

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        startForeground(1, NotificationHelper.createNotification(this))

        val modelPath = AppConfig.getVoskModelDir(application).absolutePath

        hotwordRecognizer = HotwordRecognizer(
            context = this,
            modelPath = modelPath
        ) { _audioBuffer ->
            Logger.d("Wake word détecté")

            // Stop Hotword et lancer VoiceCommandService
            hotwordRecognizer.stopListening()
            val intent = Intent(this, com.navarro.voice.VoiceCommandService::class.java)
            startService(intent)
        }

        hotwordRecognizer.startListening()
        Logger.d("HotwordService démarré")
    }

    override fun onDestroy() {
        hotwordRecognizer.stopListening()
        Logger.d("HotwordService arrêté")
        super.onDestroy()
    }
}
