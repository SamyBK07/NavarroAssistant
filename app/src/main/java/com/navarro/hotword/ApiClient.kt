package com.navarro.hotword

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

object ApiClient {

    private val client = OkHttpClient()

    fun sendToMistral(prompt: String, callback: (String) -> Unit) {

        val apiKey = "PD85t8aUMKTkZGDlAWhOWyxywwYRkSq1"

        val json = JSONObject()
        json.put("model", "mistral-small")
        json.put("prompt", prompt)

        val body = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            json.toString()
        )

        val request = Request.Builder()
            .url("https://api.mistral.ai/v1/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                callback("Erreur réseau")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val result = JSONObject(responseBody ?: "")
                val text = result.getJSONArray("choices")
                    .getJSONObject(0)
                    .getString("text")

                callback(text)
            }
        })
    }
}
