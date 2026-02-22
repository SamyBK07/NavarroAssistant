package com.navarro.ai

import com.navarro.core.Logger

/**
 * Analyse la réponse brute de l'IA et détermine action ou texte à restituer
 */
class AIResponseParser {

    /**
     * Retourne le texte à lire ou l'action à exécuter
     */
    fun analyserReponse(reponseBrute: String): String {
        Logger.i("AIResponseParser: réponse brute -> $reponseBrute")

        // Simple parsing : si la réponse contient un mot-clé d'action, on pourrait déclencher une action
        // Pour l'instant on retourne directement le texte
        return reponseBrute.trim()
    }
}
