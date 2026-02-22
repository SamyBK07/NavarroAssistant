package com.navarro.hotword

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.navarro.core.AppConfig
import com.navarro.core.Logger

class HotwordService : Service() {
    private lateinit var hotwordRecognizer: HotwordRecognizer

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Logger.d("HotwordService démarré")
        hotwordRecognizer = HotwordRecognizer(
            context = this,
            modelPath = AppConfig.getVoskModelDir(application).absolutePath,
            onDetected = { audioData ->
                // Basculer vers VoiceCommandService
                val intent = Intent(this, VoiceCommandService::class.java).apply {
                    putExtra("audio_buffer", audioData)
                }
                startService(intent)
            }
        )
        hotwordRecognizer.startListening()
    }

    override fun onDestroy() {
        hotwordRecognizer.stopListening()
        Logger.d("HotwordService arrêté")
        super.onDestroy()
    }
}
