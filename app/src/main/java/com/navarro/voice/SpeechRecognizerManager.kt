package com.navarro.voice

import android.content.Context
import com.navarro.core.Logger
import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService

class SpeechRecognizerManager(
    private val context: Context,
    private val modelPath: String,
    private val onCommandRecognized: (String) -> Unit
) {
    private var speechService: SpeechService? = null
    private var recognizer: Recognizer? = null
    private var isListening = false

    init {
        LibVosk.setLogLevel(LogLevel.ERROR)
    }

    fun startListening() {
        if (isListening) return

        try {
            val model = Model(modelPath)
            recognizer = Recognizer(model, 16000.0f)
            speechService = SpeechService(recognizer, 16000.0f)

            val listener = object : RecognitionListener {
                override fun onPartialResult(hypothesis: String?) {}

                override fun onResult(hypothesis: String?) {
                    hypothesis?.let { onCommandRecognized(it) }
                }

                override fun onFinalResult(hypothesis: String?) {}

                override fun onError(e: Exception?) {
                    Logger.e("SpeechRecognizer error: ${e?.message}")
                }

                override fun onTimeout() {}
            }

            speechService?.startListening(listener)
            isListening = true
            Logger.d("SpeechRecognizer démarré")

        } catch (e: Exception) {
            Logger.e("SpeechRecognizer init error: ${e.message}")
        }
    }

    fun stopListening() {
        speechService?.stop()
        speechService = null
        recognizer?.close()
        recognizer = null
        isListening = false
        Logger.d("SpeechRecognizer arrêté")
    }
}
