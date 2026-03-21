package com.navarro.hotword

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.navarro.ui.MainActivity
import com.navarro.R

object NotificationHelper {
    private const val CHANNEL_ID = "NavarroAssistantChannel"
    private const val NOTIFICATION_ID = 1

    fun createNotification(context: Context): Notification {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("NavarroAssistant")
            .setContentText("Écoute en cours...")
            .setSmallIcon(R.drawable.ic_microphone) // ✅ ton icône
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE) // ✅ important
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // ✅ lockscreen
            .build()
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val manager = context.getSystemService(NotificationManager::class.java)

            val existingChannel = manager.getNotificationChannel(CHANNEL_ID)
            if (existingChannel != null) return

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Navarro Assistant",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Service d'écoute vocale en continu"
            }

            manager.createNotificationChannel(channel)
        }
    }
}
