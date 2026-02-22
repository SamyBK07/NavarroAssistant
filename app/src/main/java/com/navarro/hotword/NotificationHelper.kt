package com.navarro.hotword

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.navarro.R
import com.navarro.core.AppConfig

object NotificationHelper {

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AppConfig.NOTIFICATION_CHANNEL_ID,
                AppConfig.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Assistant vocal actif"
                setSound(null, null)
                enableVibration(false)
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun buildForegroundNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, AppConfig.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Navarro Assistant")
            .setContentText("Assistant actif")
            .setSmallIcon(R.drawable.ic_mic) // 👉 ajoute une icône micro
            .setOngoing(true)
            .setSilent(true)
            .build()
    }
}
