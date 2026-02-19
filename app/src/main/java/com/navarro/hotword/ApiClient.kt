package com.navarro.hotword

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object ApiClient {

    private val client = OkHttpClient()

    private const val API_KEY = "PD85t8aUMKTkZGDlAWhOWyxywwYRkSq1"

    fun sendToMistral(prompt: String, callback: (String) -> Unit) {

        val json = JSONObject().apply {
            put("model", "mistral-small-latest")

            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
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
                callback("Erreur réseau")
            }

            override fun onResponse(call: Call, response: Response) {

                val responseBody = response.body?.string()

                if (!response.isSuccessful || responseBody == null) {
                    callback("Erreur API")
                    return
                }

                try {
                    val result = JSONObject(responseBody)
                    val text = result
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                    callback(text)

                } catch (e: Exception) {
                    callback("Erreur lecture réponse")
                }
            }
        })
    }
}
