package com.navarro.ai

import com.navarro.core.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object RemoteAIManager {
    private val client = OkHttpClient()
    private const val API_URL = "https://api.mistral.ai/v1/chat/completions"

    suspend fun sendToMistral(prompt: String): String = withContext(Dispatchers.IO) {
        val mediaType = "application/json".toMediaType()

        val jsonBody = JSONObject()
            .put("model", "mistral-tiny")
            .put("messages", listOf(
                JSONObject()
                    .put("role", "user")
                    .put("content", prompt)
            ))

        val requestBody = jsonBody.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(API_URL)
            .post(requestBody)
            .addHeader("Authorization", "Bearer ${AppConfig.MISTRAL_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Erreur API Mistral: ${response.code}")
                }

                val responseBody = response.body?.string()
                    ?: throw IOException("Réponse vide")

                return@withContext JSONObject(responseBody)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
            }
        } catch (e: Exception) {
            return@withContext "Erreur: ${e.message}"
        }
    }
}
