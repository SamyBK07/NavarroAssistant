package com.navarro.ai

import com.navarro.core.Logger
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.CopyOnWriteArrayList

class PromptBuilder {

    private val historique = CopyOnWriteArrayList<JSONObject>()

    private val systemMessage = JSONObject()
        .put("role", "system")
        .put("content", "Tu es Navarro, assistant vocal style Jarvis. Réponds brièvement et naturellement.")

    fun construireMessages(commande: String): JSONArray {
        val userMsg = JSONObject()
            .put("role", "user")
            .put("content", commande)

        historique.add(userMsg)

        // Limite mémoire conversationnelle stricte
        if (historique.size > 20) {
            historique.removeAt(0)
        }

        val messages = JSONArray()
        messages.put(systemMessage)

        historique.forEach { messages.put(it) }

        Logger.i("PromptBuilder: ${historique.size} messages")
        return messages
    }

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
