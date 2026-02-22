package com.navarro.ai

import com.navarro.core.Logger

/**
 * Construit le prompt complet pour l'IA
 */
class PromptBuilder {

    private val contexteHistorique = mutableListOf<String>()

    /**
     * Ajoute la commande au contexte et génère le prompt final
     */
    fun construirePrompt(commande: String): String {
        contexteHistorique.add("Utilisateur: $commande")

        // Limite contexte pour éviter prompt trop long
        val derniereContexte = contexteHistorique.takeLast(10).joinToString("\n")

        val promptFinal = """
            Tu es NavarroAssistant, un assistant vocal personnel.
            Réponds de manière concise, polie et utile.
            
            Contexte:
            $derniereContexte
            
            Réponse:
        """.trimIndent()

        Logger.i("PromptBuilder: prompt généré -> $promptFinal")
        return promptFinal
    }

    /**
     * Optionnel: nettoyer le contexte
     */
    fun reinitialiserContexte() {
        contexteHistorique.clear()
    }
}
