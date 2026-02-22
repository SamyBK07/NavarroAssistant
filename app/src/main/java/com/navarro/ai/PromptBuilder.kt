package com.navarro.ai

import com.navarro.core.Logger
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Construit le contexte conversationnel pour Mistral Chat API
 */
class PromptBuilder {

    private val historique = CopyOnWriteArrayList<JSONObject>()

    private val systemMessage = JSONObject()
        .put("role", "system")
        .put("content", "Tu es Navarro, assistant vocal style Jarvis. Réponds brièvement et naturellement.")

    /**
     * Ajoute message utilisateur et retourne le tableau messages complet
     */
    fun construireMessages(commande: String): JSONArray {
        val userMsg = JSONObject()
            .put("role", "user")
            .put("content", commande)

        historique.add(userMsg)

        // Limite mémoire conversationnelle
        val derniers = historique.takeLast(10)

        val messages = JSONArray()
        messages.put(systemMessage)

        derniers.forEach { messages.put(it) }

        Logger.i("PromptBuilder: messages générés -> $messages")
        return messages
    }

    /**
     * Ajoute la réponse IA pour continuité conversationnelle
     */
    fun ajouterReponseIA(reponse: String) {
        val aiMsg = JSONObject()
            .put("role", "assistant")
            .put("content", reponse)

        historique.add(aiMsg)
    }

    fun reset() {
        historique.clear()
    }
}
