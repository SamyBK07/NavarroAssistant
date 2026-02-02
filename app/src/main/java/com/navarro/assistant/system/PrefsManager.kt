package com.navarro.assistant.system

import android.content.Context
import android.content.SharedPreferences

object PrefsManager {

    private const val PREF_NAME = "navarro_prefs"
    private const val KEY_MISTRAL_API = "PD85t8aUMKTkZGDlAWhOWyxywwYRkSq1"

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveMistralApiKey(context: Context, apiKey: String) {
        prefs(context).edit()
            .putString(KEY_MISTRAL_API, apiKey)
            .apply()
    }

    fun getMistralApiKey(context: Context): String? {
        return prefs(context).getString(KEY_MISTRAL_API, null)
    }
}
