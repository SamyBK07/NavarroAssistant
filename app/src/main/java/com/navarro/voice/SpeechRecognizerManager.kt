package com.navarro.voice

import android.content.Context
import com.navarro.core.Logger
import org.vosk.LibVosk
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
        LibVosk.setLogLevel(0)
    }

    fun startListening() {
        if (isListening) return
        try {
            val model = Model(modelPath)
            recognizer = Recognizer(model, 16000f)
            speechService = SpeechService(recognizer, 16000f).apply {
                setListener(object : RecognitionListener {
                    override fun onPartialResult(hypothesis: String?) {}
                    override fun onResult(hypothesis: String?) {
                        hypothesis?.let { onCommandRecognized(it) }
                    }
                    override fun onError(e: Exception?) {
                        Logger.e("Erreur SpeechRecognizer: ${e?.message}")
                    }
                    override fun onTimeout() {}
                })
            }
            speechService?.startListening()
            isListening = true
            Logger.d("SpeechRecognizer démarré")
        } catch (e: Exception) {
            Logger.e("Erreur initialisation SpeechRecognizer: ${e.message}")
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
