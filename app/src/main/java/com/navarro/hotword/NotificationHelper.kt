package com.navarro.hotword

import android.app.*
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.navarro.R

object NotificationHelper {

    private const val CHANNEL_ID = "navarro_hotword_channel"

    fun createNotification(context: Context): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Navarro Hotword Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Navarro actif")
            .setContentText("Écoute en arrière-plan")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }
}
