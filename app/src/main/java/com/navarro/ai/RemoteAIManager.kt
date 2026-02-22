package com.navarro.ai

import com.navarro.core.AppConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object RemoteAIManager {
    private val client = OkHttpClient()
    private const val API_URL = "https://api.mistral.ai/v1/chat/completions"

    /**
     * Envoie une requête à l'API Mistral et retourne la réponse.
     * @param prompt Le texte à envoyer à Mistral.
     * @return La réponse de Mistral ou un message d'erreur.
     */
    suspend fun sendToMistral(prompt: String): String {
        val mediaType = "application/json".toMediaType()
        val requestBody = """
            {
                "model": "mistral-tiny",
                "messages": [
                    {"role": "user", "content": "$prompt"}
                ]
            }
        """.trimIndent().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(API_URL)
            .post(requestBody)
            .addHeader("Authorization", "Bearer ${AppConfig.MISTRAL_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("Erreur API Mistral: ${response.code} - ${response.message}")
            }
            val responseBody = response.body?.string() ?: throw IOException("Réponse vide")
            // Parse la réponse JSON pour extraire le contenu
            JSONObject(responseBody)
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
        } catch (e: Exception) {
            "Désolé, je n'ai pas pu obtenir de réponse. Erreur: ${e.message}"
        }
    }
}
