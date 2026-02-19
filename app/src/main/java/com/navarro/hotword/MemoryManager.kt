package com.navarro.hotword

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object MemoryManager {

    private const val PREF_NAME = "navarro_memory"
    private const val KEY_HISTORY = "history"
    private const val MAX_MESSAGES = 10

    fun addMessage(context: Context, role: String, content: String) {

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val history = getHistoryArray(context)

        val message = JSONObject().apply {
            put("role", role)
            put("content", content)
        }

        history.put(message)

        // limite mémoire
        while (history.length() > MAX_MESSAGES) {
            history.remove(0)
        }

        prefs.edit().putString(KEY_HISTORY, history.toString()).apply()
    }

    fun getFormattedHistory(context: Context): String {

        val history = getHistoryArray(context)
        val builder = StringBuilder()

        for (i in 0 until history.length()) {
            val msg = history.getJSONObject(i)
            builder.append("${msg.getString("role")}: ${msg.getString("content")}\n")
        }

        return builder.toString()
    }

    private fun getHistoryArray(context: Context): JSONArray {

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_HISTORY, null)

        return if (saved != null) JSONArray(saved) else JSONArray()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_HISTORY)
            .apply()
    }
}
