package com.navarro.hotword

import android.content.Context
import com.navarro.core.AppConfig
import com.navarro.core.Logger
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import java.io.File

class HotwordRecognizer(
    private val context: Context,
    private val onHotwordDetected: () -> Unit
) : RecognitionListener {

    private var model: Model? = null
    private var recognizer: Recognizer? = null
    private var speechService: SpeechService? = null

    private val sampleRate = 16000f

    fun start() {
        try {
            val modelPath = File(
                context.filesDir,
                "${AppConfig.MODELS_DIR}/${AppConfig.MODEL_NAME}"
            )

            if (!modelPath.exists()) {
                Logger.e("VOSK model not found")
                return
            }

            Logger.i("Loading VOSK model...")
            model = Model(modelPath.absolutePath)

            val grammar = "[\"navarro\", \"[unk]\"]"
            recognizer = Recognizer(model, sampleRate, grammar)

            speechService = SpeechService(recognizer, sampleRate)
            speechService?.startListening(this)

            Logger.i("Hotword listening started")

        } catch (e: Exception) {
            Logger.e("Hotword start error", e)
        }
    }

    fun stop() {
        try {
            speechService?.stop()
            speechService?.shutdown()
            recognizer?.close()
            model?.close()

            speechService = null
            recognizer = null
            model = null

            Logger.i("Hotword stopped")

        } catch (e: Exception) {
            Logger.e("Hotword stop error", e)
        }
    }

    override fun onPartialResult(hypothesis: String?) {
        hypothesis?.let {
            val text = extractText(it)
            Logger.d("Partial: $text")

            if (text.contains("navarro", true)) {
                Logger.i("Hotword detected!")
                onHotwordDetected.invoke()
            }
        }
    }

    override fun onResult(hypothesis: String?) {
        // Pas nécessaire pour wake word
    }

    override fun onFinalResult(hypothesis: String?) {
        // Pas nécessaire pour wake word
    }

    override fun onError(e: Exception?) {
        Logger.e("Hotword error", e)
    }

    override fun onTimeout() {
        Logger.w("Hotword timeout")
    }

    private fun extractText(json: String): String {
        return try {
            JSONObject(json).optString("partial", "")
        } catch (e: Exception) {
            ""
        }
    }
}
