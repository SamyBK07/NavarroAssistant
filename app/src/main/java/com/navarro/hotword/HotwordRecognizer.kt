package com.navarro.hotword

import android.content.Context
import com.navarro.core.Logger
import org.vosk.LibVosk
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.SpeechStreamService
import org.vosk.android.StorageService

class HotwordRecognizer(
    private val context: Context,
    private val modelPath: String,
    private val onDetected: (ShortArray) -> Unit
) {
    private var speechService: SpeechStreamService? = null
    private var recognizer: Recognizer? = null
    private val audioBuffer = ShortArray(4096)
    private var isListening = false

    init {
        LibVosk.setLogLevel(0) // Désactive les logs VOSK
        StorageService.unpack(context, "hotword_grammar.json", "grammar", true)
    }

    fun startListening() {
        if (isListening) return
        try {
            val model = Model(modelPath)
            recognizer = Recognizer(model, 16000f, "[\"navarro\", \"[unk]\"]")
            speechService = SpeechStreamService(recognizer, 16000f, "grammar").apply {
                setListener(object : RecognitionListener {
                    override fun onPartialResult(hypothesis: String?) {}
                    override fun onResult(hypothesis: String?) {
                        if (hypothesis?.contains("navarro") == true) {
                            onDetected(audioBuffer)
                        }
                    }
                    override fun onError(e: Exception?) {
                        Logger.e("Erreur HotwordRecognizer: ${e?.message}")
                    }
                    override fun onTimeout() {}
                })
            }
            speechService?.startListening()
            isListening = true
            Logger.d("HotwordRecognizer démarré")
        } catch (e: Exception) {
            Logger.e("Erreur initialisation HotwordRecognizer: ${e.message}")
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
