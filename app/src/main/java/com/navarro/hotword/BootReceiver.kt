package com.navarro.hotword

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.navarro.core.Logger

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Logger.d("Redémarrage après boot")
            context.startService(Intent(context, HotwordService::class.java))
        }
    }
}
