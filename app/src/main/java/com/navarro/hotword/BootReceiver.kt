package com.navarro.hotword

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.navarro.core.Logger

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Logger.d("Redémarrage après boot")

            val serviceIntent = Intent(context, HotwordService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}
