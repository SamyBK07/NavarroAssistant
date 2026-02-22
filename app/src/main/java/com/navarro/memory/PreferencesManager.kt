package com.navarro.memory

import android.content.Context
import android.content.SharedPreferences
import com.navarro.core.Logger

/**
 * Gère les préférences utilisateur (voix, surnom, etc.)
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("navarro_prefs", Context.MODE_PRIVATE)

    fun sauvegarderPreference(cle: String, valeur: String) {
        prefs.edit().putString(cle, valeur).apply()
        Logger.i("PreferencesManager: sauvegardé [$cle] -> $valeur")
    }

    fun recupererPreference(cle: String, defaut: String = ""): String {
        val valeur = prefs.getString(cle, defaut) ?: defaut
        Logger.i("PreferencesManager: récupéré [$cle] -> $valeur")
        return valeur
    }

    fun reinitialiserPreferences() {
        prefs.edit().clear().apply()
        Logger.i("PreferencesManager: préférences réinitialisées")
    }
}
