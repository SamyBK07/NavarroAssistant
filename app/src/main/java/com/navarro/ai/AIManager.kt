package com.navarro.ai

import android.content.Context
import com.navarro.core.Logger
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class AIManager(private val contexte: Context) {

    private val client = OkHttpClient()

    // 👉 Mets ta clé ici ou via BuildConfig
    private val apiKey = "PD85t8aUMKTkZGDlAWhOWyxywwYRkSq1"

    private val systemPrompt =
        "Tu es Navarro, assistant vocal style Jarvis. Réponds brièvement et naturellement."

    fun askMistral(commande: String, callback: (String) -> Unit) {
        try {
            val body = JSONObject()
                .put("model", "mistral-small-latest")
                .put("messages", JSONArray()
                    .put(JSONObject()
                        .put("role", "system")
                        .put("content", systemPrompt))
                    .put(JSONObject()
                        .put("role", "user")
                        .put("content", commande))
                )

            val request = Request.Builder()
                .url("https://api.mistral.ai/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    body.toString()
                ))
                .build()

            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    Logger.e("Mistral API error", e)
                    callback.invoke("Je n'ai pas compris.")
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val json = JSONObject(response.body?.string() ?: "")
                        val text = json
                            .getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")

                        Logger.i("Réponse Mistral: $text")
                        callback.invoke(text.trim())

                    } catch (e: Exception) {
                        Logger.e("Parsing Mistral error", e)
                        callback.invoke("Erreur IA.")
                    }
                }
            })

        } catch (e: Exception) {
            Logger.e("Mistral request error", e)
            callback.invoke("Erreur réseau.")
        }
    }
}
