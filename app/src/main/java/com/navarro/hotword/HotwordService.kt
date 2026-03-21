package com.navarro.hotword

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.navarro.core.Logger
import org.json.JSONObject

class HotwordService : Service() {

    private lateinit var voskRecognizer: VoskRecognizer

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        startForeground(1, NotificationHelper.createNotification(this))

        voskRecognizer = VoskRecognizer(this) { result ->

            try {
                val text = JSONObject(result).optString("text")

                if (text.contains("navarro", ignoreCase = true)) {

                    Logger.d("Mot clé détecté via Vosk")

                    voskRecognizer.stopListening()

                    val intent = Intent(this, com.navarro.voice.VoiceCommandService::class.java)

                    startForegroundService(intent) // ⚠️ important Android 8+
                }

            } catch (e: Exception) {
                Logger.e("Parsing error: ${e.message}")
            }
        }

        voskRecognizer.startListening()
        Logger.d("HotwordService Vosk démarré")
    }

    override fun onDestroy() {
        voskRecognizer.stopListening()
        Logger.d("HotwordService arrêté")
        super.onDestroy()
    }
}
