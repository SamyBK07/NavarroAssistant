package com.navarro.voice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import com.navarro.ai.RemoteAIManager
import com.navarro.core.Logger
import com.navarro.hotword.VoskRecognizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VoiceCommandService : Service() {

    private var voskRecognizer: VoskRecognizer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Logger.d("VoiceCommandService démarré")

        // Utilisation de Vosk pour l'écoute des commandes
        voskRecognizer = VoskRecognizer(
            context = this,
            onResult = { command ->
                processCommand(command)
            },
            isHotwordMode = false  // Mode commande
        )

        voskRecognizer?.startListening()
    }

    private fun processCommand(command: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Logger.d("Commande reconnue : $command")

            try {
                // Envoi à l'IA distante
                val response = RemoteAIManager.sendToMistral(command)
                Logger.d("Réponse Mistral : $response")

                // Vocaliser la réponse
                TextToSpeechManager.speak(response)

                // Retour au mode mot-clé après un délai
                Handler(Looper.getMainLooper()).postDelayed({
                    stopSelf()
                    val intent = Intent(this@VoiceCommandService, com.navarro.hotword.HotwordService::class.java)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        startForegroundService(intent)
                    } else {
                        startService(intent)
                    }
                }, 3000)  // Délai de 3 secondes avant de revenir au mode mot-clé
            } catch (e: Exception) {
                Logger.e("Erreur lors du traitement de la commande : ${e.message}")
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
        voskRecognizer?.stopListening()
        Logger.d("VoiceCommandService arrêté")
        super.onDestroy()
    }
}
