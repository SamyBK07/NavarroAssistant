package com.navarro.ai

import android.content.Context
import com.navarro.core.Logger

class AIManager(private val contexte: Context) {

    private val localLLM = LocalLLMManager(contexte)
    private val promptBuilder = PromptBuilder()
    private val responseParser = AIResponseParser()

    /**
     * Traite une commande vocale ou question et retourne le résultat
     */
    fun traiterCommande(commande: String): String {
        Logger.i("AIManager: traitement de la commande -> $commande")

        // Construction du prompt avec contexte
        val prompt = promptBuilder.construirePrompt(commande)

        // Appel modèle local ou LLM
        val reponseBrute = localLLM.genererReponse(prompt)

        // Analyse de la réponse pour déterminer action ou texte
        val resultat = responseParser.analyserReponse(reponseBrute)

        Logger.i("AIManager: résultat -> $resultat")
        return resultat
    }
}
