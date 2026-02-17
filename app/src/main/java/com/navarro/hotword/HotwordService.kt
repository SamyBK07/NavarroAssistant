package com.navarro.hotword

import ai.picovoice.porcupine.PorcupineManager
import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class HotwordService : Service() {

    private lateinit var porcupineManager: PorcupineManager

    override fun onCreate() {
        super.onCreate()

        startForeground(1, createNotification())

        porcupineManager = PorcupineManager.Builder()
            .setAccessKey("FYz0s4zjZ8mszfhVSdE5kh338bNcFa9EY7gEphcShwVEK5vxBfSGTA==")
            .setKeywordPath("navarro.ppn")
            .setSensitivity(0.7f)
            .build(this) { _ ->
                Log.d("NAVARRO", "Mot détecté 🔥")
            }

        porcupineManager.start()
    }

    override fun onDestroy() {
        porcupineManager.stop()
        porcupineManager.delete()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        val channelId = "navarro_channel"

        val channel = NotificationChannel(
            channelId,
            "Navarro Service",
            NotificationManager.IMPORTANCE_LOW
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Navarro actif")
            .setContentText("Écoute en cours...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()
    }
}
