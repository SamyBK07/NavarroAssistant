package com.navarro.hotword

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object SmartApi {  // Note : `object` et non `class` pour éviter les doublons

    fun send(context: Context, prompt: String, callback: (String) -> Unit) {
        // Récupère l'historique depuis MemoryManager
        val history = MemoryManager.getHistoryArray(context)

        // Construit le JSON avec l'historique + le nouveau message
        val json = JSONObject().apply {
            put("model", "mistral-small-latest")

            val messagesArray = JSONArray()
            // Ajoute l'historique existant
            for (i in 0 until history.length()) {
                messagesArray.put(history.getJSONObject(i))
            }
            // Ajoute le nouveau message de l'utilisateur
            messagesArray.put(JSONObject().apply {
                put("role", "user")
                put("content", prompt)
            })

            put("messages", messagesArray)
        }

        // Appelle ApiClient pour envoyer la requête
        ApiClient.sendToMistral(context, json.toString()) { response ->
            // Sauvegarde la réponse de l'assistant dans l'historique
            MemoryManager.addMessage(context, "assistant", response)
            callback(response)
        }
    }
}
