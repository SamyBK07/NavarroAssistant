package com.navarro.assistant.system

import android.content.Context
import android.util.Log
import com.navarro.assistant.ai.MistralClient

/**
 * Cerveau local de l'assistant
 * - reçoit le texte (STT)
 * - décide quoi en faire
 * - appelle l'IA
 * - renvoie la réponse (TTS plus tard)
 */
object TextDispatcher {

    private var mistralClient: MistralClient? = null

    /**
     * À appeler UNE FOIS au démarrage du service
     */
    fun init(context: Context, apiKey: String) {
        mistralClient = MistralClient(apiKey)
        Log.d("Navarro-Dispatcher", "Dispatcher initialisé")
    }

    /**
     * Point d'entrée UNIQUE pour tout texte utilisateur
     */
    fun onUserText(text: String) {
        Log.d("Navarro-Dispatcher", "Texte reçu : $text")

        if (mistralClient == null) {
            Log.e("Navarro-Dispatcher", "MistralClient non initialisé")
            return
        }

        // Envoi du texte à l'IA
        mistralClient?.sendPrompt(text) { aiResponse ->
            onAIResponse(aiResponse)
        }
    }

    /**
     * Réception de la réponse IA
     */
    private fun onAIResponse(response: String) {
        Log.d("Navarro-Dispatcher", "Réponse IA : $response")

        // 👉 PLUS TARD :
        // - AndroidTTSManager.speak(response)
        // - ActionManager.handle(response)
        // - MemoryManager.save(...)
    }
}
