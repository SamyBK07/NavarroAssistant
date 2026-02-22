package com.navarro.voice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.SpeechService
import java.io.File

class VoiceCommandService : Service() {

    private var model: Model? = null
    private var recognizer: Recognizer? = null
    private var speechService: SpeechService? = null

    override fun onCreate() {
        super.onCreate()
        initFullRecognition()
    }

    private fun initFullRecognition() {
        Thread {
            try {
                val modelPath = File(filesDir, "vosk-model-fr")
                model = Model(modelPath.absolutePath)

                recognizer = Recognizer(model, 16000.0f)

                speechService = SpeechService(recognizer, 16000.0f)
                speechService?.startListening { result ->

                    if (recognizer?.acceptWaveForm(result.toByteArray(), result.length) == true) {
                        val finalResult = recognizer?.result
                        Log.d("Navarro", "Commande détectée: $finalResult")

                        stopSelf()
                    }
                }

            } catch (e: Exception) {
                Log.e("Navarro", "Erreur VoiceCommandService", e)
            }
        }.start()
    }

    override fun onDestroy() {
        speechService?.stop()
        recognizer?.close()
        model?.close()
        super.onDestroy()

        // Relancer hotword après commande
        val intent = Intent(this, com.navarro.hotword.HotwordService::class.java)
        startForegroundService(intent)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
