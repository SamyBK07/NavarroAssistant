package com.navarro.ai

import android.content.Context
import com.navarro.core.Logger
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

/**
 * Gestion du modèle Mistral via API
 */
class LocalLLMManager(private val contexte: Context) {

    private val client = OkHttpClient()

    // URL de l'API Mistral locale ou distante
    private val apiUrl = "http://127.0.0.1:5000/generate" // <- adapter selon ton setup

    /**
     * Génère une réponse à partir d'un prompt via Mistral API
     */
    fun genererReponse(prompt: String): String {
        Logger.i("LocalLLMManager: génération réponse pour prompt -> $prompt")

        return try {
            val jsonRequest = JSONObject().apply {
                put("prompt", prompt)
                put("max_tokens", 200)        // adapter selon besoin
                put("temperature", 0.7)
            }

            val body = jsonRequest.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(apiUrl)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Logger.e("Mistral API erreur HTTP: ${response.code}")
                    return "Erreur lors de la génération"
                }

                val respJson = JSONObject(response.body?.string() ?: "")
                val texte = respJson.optString("text", "Réponse vide")
                Logger.i("Mistral API réponse -> $texte")
                texte
            }

        } catch (e: IOException) {
            Logger.e("Erreur réseau Mistral", e)
            "Erreur réseau"
        } catch (e: Exception) {
            Logger.e("Erreur génération Mistral", e)
            "Erreur inattendue"
        }
    }
}
