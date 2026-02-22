package com.navarro.voice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.navarro.core.AppConfig
import com.navarro.core.Logger
import com.navarro.hotword.HotwordService

class VoiceCommandService : Service() {

    private var speechManager: SpeechRecognizerManager? = null

    override fun onCreate() {
        super.onCreate()
        Logger.i("VoiceCommandService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            AppConfig.ACTION_START_COMMAND -> startCommandListening()
            AppConfig.ACTION_STOP_COMMAND -> stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun startCommandListening() {
        if (speechManager != null) return

        speechManager = SpeechRecognizerManager(this,
            onResult = { text ->
                Logger.i("Command: $text")
                restartHotword()
            },
            onError = {
                Logger.e("Command recognition error")
                restartHotword()
            }
        )

        speechManager?.start()
    }

    private fun restartHotword() {
        speechManager?.stop()
        speechManager = null

        val intent = Intent(this, HotwordService::class.java).apply {
            action = AppConfig.ACTION_START_HOTWORD
        }
        startForegroundService(intent)

        stopSelf()
    }

    override fun onDestroy() {
        speechManager?.stop()
        Logger.i("VoiceCommandService destroyed")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
