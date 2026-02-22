package com.navarro.hotword

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.SpeechService
import java.io.File
import java.io.InputStreamReader

class HotwordService : Service() {

    private var model: Model? = null
    private var recognizer: Recognizer? = null
    private var speechService: SpeechService? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(1, NotificationHelper.createNotification(this))
        initRecognizer()
    }

    private fun initRecognizer() {
        Thread {
            try {
                val modelPath = File(filesDir, "vosk-model-fr")

                if (!modelPath.exists()) {
                    Log.e("Navarro", "Modèle introuvable")
                    return@Thread
                }

                model = Model(modelPath.absolutePath)

                val grammar = assets.open("hotword_grammar.json")
                    .bufferedReader()
                    .use { it.readText() }

                recognizer = Recognizer(model, 16000.0f, grammar)

                speechService = SpeechService(recognizer, 16000.0f)
                speechService?.startListening { result ->
                    if (result.contains("navarro")) {
                        Log.d("Navarro", "Mot clé détecté")
                    }
                }

            } catch (e: Exception) {
                Log.e("Navarro", "Erreur init recognizer", e)
            }
        }.start()
    }

    override fun onDestroy() {
        speechService?.stop()
        recognizer?.close()
        model?.close()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
