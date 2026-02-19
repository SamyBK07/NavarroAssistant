package com.navarro.hotword

import android.content.Context

object SmartApi {

    fun send(context: Context, prompt: String, callback: (String) -> Unit) {

        // Sauvegarde message utilisateur
        MemoryManager.addMessage(context, "user", prompt)

        val history = MemoryManager.getFormattedHistory(context)

        val enrichedPrompt = """
Voici la conversation précédente :

$history

Réponds naturellement à la dernière demande.
""".trimIndent()

        ApiClient.sendToMistral(enrichedPrompt) { response ->

            // Sauvegarde réponse IA
            MemoryManager.addMessage(context, "assistant", response)

            callback(response)
        }
    }
}
