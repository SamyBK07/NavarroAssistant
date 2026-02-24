package com.navarro.hotword

import android.content.Context
import com.navarro.core.Logger
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService

class HotwordRecognizer(
    private val context: Context,
    private val modelPath: String,
    private val onDetected: (ShortArray) -> Unit
) {
    private var speechService: SpeechService? = null
    private var recognizer: Recognizer? = null
    private val audioBuffer = ShortArray(4096)
    private var isListening = false

    fun startListening() {
        if (isListening) return

        try {
            val model = Model(modelPath)
            recognizer = Recognizer(model, 16000.0f, """["navarro","[unk]"]""")
            speechService = SpeechService(recognizer, 16000.0f)

            val listener = object : RecognitionListener {

                override fun onPartialResult(hypothesis: String?) {}

                override fun onResult(hypothesis: String?) {
                    if (hypothesis?.contains("navarro", ignoreCase = true) == true) {
                        onDetected(audioBuffer)
                    }
                }

                override fun onFinalResult(hypothesis: String?) {}

                override fun onError(e: Exception?) {
                    Logger.e("HotwordRecognizer error: ${e?.message}")
                }

                override fun onTimeout() {}
            }

            speechService?.startListening(listener)
            isListening = true
            Logger.d("HotwordRecognizer démarré")

        } catch (e: Exception) {
            Logger.e("HotwordRecognizer init error: ${e.message}")
        }
    }

    fun stopListening() {
        speechService?.stop()
        speechService = null
        recognizer?.close()
        recognizer = null
        isListening = false
        Logger.d("HotwordRecognizer arrêté")
    }
}
