package com.navarro.memory

import android.content.Context
import android.content.SharedPreferences
import com.navarro.core.Logger

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("NavarroAssistantPrefs", Context.MODE_PRIVATE)

    fun savePreference(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
        Logger.d("Préférence sauvegardée: $key=$value")
    }

    fun getPreference(key: String, default: String = ""): String {
        return prefs.getString(key, default) ?: default
    }
}
