package com.navarro.assistant.ai

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MistralClient(private val apiKey: String) {

    private val client = OkHttpClient()
    private val mediaType = "application/json".toMediaType()

    private val API_URL = "https://api.mistral.ai/v1/chat/completions"

    fun sendPrompt(
        userText: String,
        callback: (String) -> Unit
    ) {
        val json = buildRequestBody(userText)

        val request = Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(json.toRequestBody(mediaType))
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("Navarro-Mistral", "Erreur réseau", e)
                callback("Erreur de connexion à l'IA")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback("Erreur IA : ${it.code}")
                        return
                    }

                    val body = it.body?.string() ?: ""
                    val aiText = extractResponse(body)
                    callback(aiText)
                }
            }
        })
    }

    private fun buildRequestBody(text: String): String {
        val message = JSONObject()
        message.put("role", "user")
        message.put("content", text)

        val messages = JSONArray()
        messages.put(message)

        val root = JSONObject()
        root.put("model", "mistral-small-latest")
        root.put("messages", messages)

        return root.toString()
    }

    private fun extractResponse(json: String): String {
        return try {
            val root = JSONObject(json)
            val choices = root.getJSONArray("choices")
            val message = choices.getJSONObject(0).getJSONObject("message")
            message.getString("content")
        } catch (e: Exception) {
            Log.e("Navarro-Mistral", "Erreur parsing", e)
            "Réponse IA illisible"
        }
    }
}
