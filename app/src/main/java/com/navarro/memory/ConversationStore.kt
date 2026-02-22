package com.navarro.memory

import android.content.Context
import android.util.Base64
import com.navarro.core.Logger
import java.io.File

/**
 * Stocke l'historique chiffré des conversations
 */
class ConversationStore(private val context: Context) {

    private val fichierHistorique = File(context.filesDir, "conversation_history.txt")

    fun ajouterMessage(message: String) {
        try {
            val ligne = Base64.encodeToString(message.toByteArray(), Base64.NO_WRAP) + "\n"
            fichierHistorique.appendText(ligne)
            Logger.i("ConversationStore: message ajouté")
        } catch (e: Exception) {
            Logger.e("ConversationStore: erreur ajout message", e)
        }
    }

    fun recupererHistorique(): List<String> {
        return try {
            if (!fichierHistorique.exists()) return emptyList()
            fichierHistorique.readLines().map {
                String(Base64.decode(it, Base64.NO_WRAP))
            }
        } catch (e: Exception) {
            Logger.e("ConversationStore: erreur lecture historique", e)
            emptyList()
        }
    }

    fun reinitialiserHistorique() {
        try {
            if (fichierHistorique.exists()) {
                fichierHistorique.delete()
                Logger.i("ConversationStore: historique réinitialisé")
            }
        } catch (e: Exception) {
            Logger.e("ConversationStore: erreur réinitialisation historique", e)
        }
    }
}
