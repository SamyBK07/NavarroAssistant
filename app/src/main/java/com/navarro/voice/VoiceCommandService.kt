package com.navarro.voice

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.navarro.ai.AIManager
import com.navarro.core.AppConfig
import com.navarro.core.Logger
import com.navarro.hotword.HotwordService
import com.navarro.hotword.NotificationHelper

class VoiceCommandService : Service() {

    private var speechManager: SpeechRecognizerManager? = null
    private lateinit var ttsManager: TextToSpeechManager
    private lateinit var aiManager: AIManager

    private val timeoutHandler = Handler(Looper.getMainLooper())
    private val timeoutRunnable = Runnable {
        Logger.w("Timeout commande")
        restartHotword()
    }

    override fun onCreate() {
        super.onCreate()

        NotificationHelper.createChannel(this)
        startForeground(
            AppConfig.NOTIFICATION_COMMAND_ID,
            NotificationHelper.buildCommandNotification(this)
        )

        ttsManager = TextToSpeechManager(this)
        aiManager = AIManager(this)

        Logger.i("VoiceCommandService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            AppConfig.ACTION_START_COMMAND -> startCommandListening()
            AppConfig.ACTION_STOP_COMMAND -> stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun startCommandListening() {
        if (speechManager != null) return

        speechManager = SpeechRecognizerManager(
            context = this,
            onResult = { text ->
                Logger.i("Commande: $text")
                timeoutHandler.removeCallbacks(timeoutRunnable)
                traiterCommande(text)
            },
            onError = {
                Logger.e("Erreur reconnaissance commande")
                restartHotword()
            }
        )

        speechManager?.start()

        // Timeout sécurité 8s
        timeoutHandler.postDelayed(timeoutRunnable, 8000)
    }

    /**
     * Pipeline IA → réponse vocale
     */
    private fun traiterCommande(commande: String) {
        aiManager.askMistral(commande) { response ->
            Logger.i("Réponse IA: $response")

            ttsManager.speak(response)
            restartHotword()
        }
    }

    private fun restartHotword() {
        speechManager?.stop()
        speechManager = null

        val intent = Intent(this, HotwordService::class.java).apply {
            action = AppConfig.ACTION_START_HOTWORD
        }
        startForegroundService(intent)

        stopSelf()
    }

    override fun onDestroy() {
        timeoutHandler.removeCallbacks(timeoutRunnable)
        speechManager?.stop()
        Logger.i("VoiceCommandService destroyed")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
