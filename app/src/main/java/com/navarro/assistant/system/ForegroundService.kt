package com.navarro.assistant.system

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.navarro.assistant.R
import com.navarro.assistant.stt.AndroidSTTManager

class ForegroundService : Service() {

    private lateinit var sttManager: AndroidSTTManager
    private val CHANNEL_ID = "NavarroAssistantChannel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, buildNotification("Assistant actif"))

        // 🔴 AJOUT : initialisation du cerveau
        TextDispatcher.init(
            context = this,
            apiKey = "TA_CLE_API_MISTRAL"
        )

        // Initialisation du STT natif
        sttManager = AndroidSTTManager(this)
        sttManager.startListening()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sttManager.stopListening()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Navarro Assistant Service",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Service pour écoute continue et TTS"
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(contentText: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Navarro Assistant")
            .setContentText(contentText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()
    }
}
