package com.navarro.hotword

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.navarro.R
import com.navarro.ui.MainActivity

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
            .setContentTitle("Navarro Assistant")
            .setContentText("Écoute en cours...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)  // Icône système par défaut
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
