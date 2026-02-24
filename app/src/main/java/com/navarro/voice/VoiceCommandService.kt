package com.navarro.voice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.navarro.ai.RemoteAIManager
import com.navarro.core.AppConfig
import com.navarro.core.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VoiceCommandService : Service() {

    private lateinit var speechRecognizerManager: SpeechRecognizerManager

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Logger.d("VoiceCommandService démarré")

        val modelPath = AppConfig.getVoskModelDir(application).absolutePath

        speechRecognizerManager = SpeechRecognizerManager(
            context = this,
            modelPath = modelPath
        ) { command ->
            processCommand(command)
        }

        speechRecognizerManager.startListening()
    }

    private fun processCommand(command: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Logger.d("Commande reconnue : $command")
            val response = RemoteAIManager.sendToMistral(command)
            Logger.d("Réponse Mistral : $response")
            // TODO: vocaliser ou afficher UI

            // Stop service et revenir au HotwordService
            stopSelf()
            val intent = Intent(this@VoiceCommandService, com.navarro.hotword.HotwordService::class.java)
            startService(intent)
        }
    }

    override fun onDestroy() {
        speechRecognizerManager.stopListening()
        Logger.d("VoiceCommandService arrêté")
        super.onDestroy()
    }
}
