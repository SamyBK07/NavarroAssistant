package com.navarro.hotword

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object MemoryManager {

    // Nom du fichier de préférences pour stocker l'historique
    private const val PREF_NAME = "navarro_memory_prefs"
    private const val KEY_HISTORY = "conversation_history"

    // Limite le nombre de messages stockés (pour éviter une croissance infinie)
    private const val MAX_MESSAGES = 20

    /**
     * Ajoute un message à l'historique.
     * @param context Contexte Android pour accéder aux SharedPreferences.
     * @param role Rôle de l'émetteur ("user" ou "assistant").
     * @param content Contenu du message.
     */
    fun addMessage(context: Context, role: String, content: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val history = getHistoryArray(context)

        // Crée un nouvel objet JSON pour le message
        val message = JSONObject().apply {
            put("role", role)
            put("content", content)
            put("timestamp", System.currentTimeMillis()) // Ajoute un timestamp
        }

        // Ajoute le message à l'historique
        history.put(message)

        // Limite la taille de l'historique
        while (history.length() > MAX_MESSAGES) {
            history.remove(0) // Supprime les anciens messages
        }

        // Sauvegarde l'historique mis à jour
        prefs.edit().putString(KEY_HISTORY, history.toString()).apply()
    }

    /**
     * Récupère l'historique sous forme de JSONArray.
     * @param context Contexte Android.
     * @return JSONArray contenant l'historique des messages.
     */
    fun getHistoryArray(context: Context): JSONArray {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val savedHistory = prefs.getString(KEY_HISTORY, null)

        return if (savedHistory != null) {
            try {
                JSONArray(savedHistory)
            } catch (e: Exception) {
                JSONArray() // Retourne un JSONArray vide en cas d'erreur
            }
        } else {
            JSONArray()
        }
    }

    /**
     * Récupère l'historique formaté sous forme de String.
     * @param context Contexte Android.
     * @return String formatée avec l'historique des conversations.
     */
    fun getFormattedHistory(context: Context): String {
        val history = getHistoryArray(context)
        val builder = StringBuilder()

        for (i in 0 until history.length()) {
            val message = history.getJSONObject(i)
            val role = message.getString("role")
            val content = message.getString("content")
            builder.append("$role: $content\n")
        }

        return if (builder.isNotEmpty()) builder.toString() else "Aucun historique."
    }

    /**
     * Efface tout l'historique.
     * @param context Contexte Android.
     */
    fun clearHistory(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_HISTORY)
            .apply()
    }

    /**
     * Récupère les X derniers messages de l'historique.
     * @param context Contexte Android.
     * @param limit Nombre maximum de messages à retourner.
     * @return JSONArray contenant les derniers messages.
     */
    fun getLastMessages(context: Context, limit: Int = MAX_MESSAGES): JSONArray {
        val history = getHistoryArray(context)
        val result = JSONArray()

        // Calcule l'index de départ pour obtenir les 'limit' derniers messages
        val startIndex = maxOf(0, history.length() - limit)

        for (i in startIndex until history.length()) {
            result.put(history.getJSONObject(i))
        }

        return result
    }
}
