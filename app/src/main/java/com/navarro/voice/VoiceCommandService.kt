package com.navarro.voice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.navarro.ai.RemoteAIManager
import com.navarro.core.Logger
import com.navarro.hotword.HotwordService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VoiceCommandService : Service() {
    private lateinit var speechRecognizerManager: SpeechRecognizerManager

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Logger.d("VoiceCommandService démarré")
        speechRecognizerManager = SpeechRecognizerManager(
            context = this,
            modelPath = com.navarro.core.AppConfig.getVoskModelDir(application).absolutePath,
            onCommandRecognized = { command ->
                processCommand(command)
            }
        )
        speechRecognizerManager.startListening()
    }

    private fun processCommand(command: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Logger.d("Commande reconnue : $command")
            val response = RemoteAIManager.sendToMistral(command)
            Logger.d("Réponse Mistral : $response")
            // TODO: Afficher la réponse dans l'UI ou la vocaliser
            stopSelf() // Arrête le service
            startService(Intent(this@VoiceCommandService, HotwordService::class.java)) // Retour en veille
        }
    }

    override fun onDestroy() {
        speechRecognizerManager.stopListening()
        Logger.d("VoiceCommandService arrêté")
        super.onDestroy()
    }
}
