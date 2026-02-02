package com.navarro.assistant.system

import android.content.Context
import android.util.Log
import com.navarro.assistant.ai.MistralClient
import com.navarro.assistant.tts.AndroidTTSManager

/**
 * Cerveau central local
 * - reçoit le texte du STT
 * - appelle l'IA (Mistral)
 * - fait parler la réponse
 */
object TextDispatcher {

    private var mistralClient: MistralClient? = null
    private var ttsManager: AndroidTTSManager? = null

    /**
     * À appeler UNE SEULE FOIS au démarrage du service
     */
    fun init(context: Context, apiKey: String) {
        mistralClient = MistralClient(apiKey)
        ttsManager = AndroidTTSManager(context)
        Log.d("Navarro-Dispatcher", "Dispatcher initialisé")
    }

    /**
     * Point d'entrée unique pour le texte utilisateur
     */
    fun onUserText(text: String) {
        Log.d("Navarro-Dispatcher", "Texte reçu : $text")

        if (mistralClient == null) {
            Log.e("Navarro-Dispatcher", "MistralClient non initialisé")
            return
        }

        mistralClient?.sendPrompt(text) { aiResponse ->
            onAIResponse(aiResponse)
        }
    }

    /**
     * Réponse IA reçue
     */
    private fun onAIResponse(response: String) {
        Log.d("Navarro-Dispatcher", "Réponse IA : $response")
        ttsManager?.speak(response)
    }
}
