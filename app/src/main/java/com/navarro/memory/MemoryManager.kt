package com.navarro.memory

import android.content.Context
import com.navarro.core.Logger

class MemoryManager(private val context: Context) {
    fun saveConversation(command: String, response: String) {
        // TODO: Sauvegarder dans un fichier ou une base de données
        Logger.d("Conversation sauvegardée: $command → $response")
    }

    fun getLastConversations(limit: Int = 5): List<Pair<String, String>> {
        // TODO: Récupérer les dernières conversations
        return emptyList()
    }
}
