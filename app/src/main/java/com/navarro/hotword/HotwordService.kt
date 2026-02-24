package com.navarro.hotword

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.navarro.core.Logger

class HotwordService : Service() {

    private lateinit var hotwordRecognizer: HotwordRecognizer

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        startForeground(1, NotificationHelper.createNotification(this))

        hotwordRecognizer = HotwordRecognizer(this) {

            Logger.d("Wake word détecté")

            hotwordRecognizer.stopListening()

            val intent = Intent(this, com.navarro.voice.VoiceCommandService::class.java)
            startService(intent)
        }

        hotwordRecognizer.startListening()
        Logger.d("HotwordService Picovoice démarré")
    }

    override fun onDestroy() {
        hotwordRecognizer.stopListening()
        Logger.d("HotwordService arrêté")
        super.onDestroy()
    }
}
