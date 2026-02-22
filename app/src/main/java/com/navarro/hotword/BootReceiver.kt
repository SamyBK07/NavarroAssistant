package com.navarro.hotword

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.navarro.core.AppConfig
import com.navarro.core.Logger

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {

            Logger.i("Boot completed → restarting HotwordService")

            val serviceIntent = Intent(context, HotwordService::class.java).apply {
                action = AppConfig.ACTION_START_HOTWORD
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}
