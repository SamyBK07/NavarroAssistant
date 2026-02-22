package com.navarro.hotword

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.navarro.core.AppConfig
import com.navarro.core.Logger
import com.navarro.voice.VoiceCommandService

class HotwordService : Service() {

    private var hotwordRecognizer: HotwordRecognizer? = null

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
        startForeground(
            AppConfig.NOTIFICATION_ID,
            NotificationHelper.buildForegroundNotification(this)
        )
        Logger.i("HotwordService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            AppConfig.ACTION_STOP_HOTWORD -> stopSelf()
            else -> startHotword()
        }

        return START_STICKY
    }

    private fun startHotword() {
        if (hotwordRecognizer != null) return

        hotwordRecognizer = HotwordRecognizer(this) {
            Logger.i("Wake word callback → starting command service")

            stopHotword()

            val intent = Intent(this, VoiceCommandService::class.java).apply {
                action = AppConfig.ACTION_START_COMMAND
            }
            startForegroundService(intent)
        }

        hotwordRecognizer?.start()
    }

    private fun stopHotword() {
        hotwordRecognizer?.stop()
        hotwordRecognizer = null
    }

    override fun onDestroy() {
        stopHotword()
        Logger.i("HotwordService destroyed")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
