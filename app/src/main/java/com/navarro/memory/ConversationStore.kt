package com.navarro.memory

import android.content.Context
import com.navarro.core.Logger
import java.io.File

class ConversationStore(private val context: Context) {
    private val conversationsFile by lazy { File(context.filesDir, "conversations.txt") }

    fun logConversation(command: String, response: String) {
        conversationsFile.appendText("$command|$response\n")
        Logger.d("Conversation loggée: $command → $response")
    }

    fun getConversations(): List<Pair<String, String>> {
        return if (conversationsFile.exists()) {
            conversationsFile.readLines().map { line ->
                val parts = line.split("|")
                if (parts.size == 2) parts[0] to parts[1] else "" to ""
            }
        } else {
            emptyList()
        }
    }
}
