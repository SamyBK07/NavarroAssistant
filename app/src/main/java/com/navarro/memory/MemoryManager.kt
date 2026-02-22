package com.navarro.memory

import android.content.Context
import android.content.SharedPreferences
import com.navarro.core.Logger

/**
 * Gère la mémoire globale de l'assistant
 */
class MemoryManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("navarro_memory", Context.MODE_PRIVATE)

    /**
     * Sauvegarde une clé/valeur
     */
    fun sauvegarderCleValeur(cle: String, valeur: String) {
        prefs.edit().putString(cle, valeur).apply()
        Logger.i("MemoryManager: sauvegardé [$cle] -> $valeur")
    }

    /**
     * Récupère une valeur par clé
     */
    fun recupererValeur(cle: String, defaut: String = ""): String {
        val valeur = prefs.getString(cle, defaut) ?: defaut
        Logger.i("MemoryManager: récupéré [$cle] -> $valeur")
        return valeur
    }

    /**
     * Supprime une clé
     */
    fun supprimerCle(cle: String) {
        prefs.edit().remove(cle).apply()
        Logger.i("MemoryManager: supprimé [$cle]")
    }

    /**
     * Efface toute la mémoire
     */
    fun reinitialiserMemoire() {
        prefs.edit().clear().apply()
        Logger.i("MemoryManager: mémoire réinitialisée")
    }
}
