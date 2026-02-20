// SmartApi.kt
package com.navarro.hotword

import android.content.Context
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object SmartApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private const val API_KEY = "PD85t8aUMKTkZGDlAWhOWyxywwYRkSq1"

    fun send(context: Context, prompt: String, callback: (String) -> Unit) {

        // Récupère l'historique depuis MemoryManager
        val history = MemoryManager.getHistoryArray(context)

        // Construit le JSON avec l'historique
        val json = JSONObject().apply {
            put("model", "mistral-small-latest")

            val messagesArray = JSONArray()
            // Ajoute l'historique existant
            for (i in 0 until history.length()) {
                messagesArray.put(history.getJSONObject(i))
            }
            // Ajoute le nouveau message de l'utilisateur
            messagesArray.put(JSONObject().apply {
                put("role", "user")
                put("content", prompt)
            })

            put("messages", messagesArray)
        }

        val body = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            json.toString()
        )

        val request = Request.Builder()
            .url("https://api.mistral.ai/v1/chat/completions")
            .addHeader("Authorization", "Bearer $API_KEY")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Erreur réseau : ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (!response.isSuccessful || responseBody == null) {
                    callback("Erreur API : ${response.message}")
                    return
                }

                try {
                    val result = JSONObject(responseBody)
                    val assistantReply = result
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                    // Sauvegarde la réponse de l'assistant dans l'historique
                    MemoryManager.addMessage(context, "assistant", assistantReply)

                    callback(assistantReply)

                } catch (e: Exception) {
                    callback("Erreur lors de la lecture de la réponse : ${e.message}")
                }
            }
        })
    }
}
