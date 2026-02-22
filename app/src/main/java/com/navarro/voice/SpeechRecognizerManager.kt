package com.navarro.voice

import android.content.Context
import com.navarro.core.AppConfig
import com.navarro.core.Logger
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.SpeechService
import org.vosk.android.RecognitionListener
import java.io.File

class SpeechRecognizerManager(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (() -> Unit)? = null
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
                Logger.e("VOSK model not found for command recognition")
                onError?.invoke()
                return
            }

            model = Model(modelPath.absolutePath)
            recognizer = Recognizer(model, sampleRate)
            speechService = SpeechService(recognizer, sampleRate)
            speechService?.startListening(this)
            Logger.i("Command recognition started")

        } catch (e: Exception) {
            Logger.e("SpeechRecognizerManager start error", e)
            onError?.invoke()
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
            Logger.i("Command recognition stopped")
        } catch (e: Exception) {
            Logger.e("SpeechRecognizerManager stop error", e)
        }
    }

    override fun onPartialResult(hypothesis: String?) {
        // Ignorer pour la commande complète
    }

    override fun onResult(hypothesis: String?) {
        hypothesis?.let {
            val text = extractText(it)
            if (text.isNotEmpty()) {
                onResult.invoke(text)
            }
        }
    }

    override fun onFinalResult(hypothesis: String?) {
        // Optionnel, on peut ignorer pour VoiceCommand
    }

    override fun onError(e: Exception?) {
        Logger.e("SpeechRecognizerManager error", e)
        onError?.invoke()
    }

    override fun onTimeout() {
        Logger.w("SpeechRecognizerManager timeout")
        onError?.invoke()
    }

    private fun extractText(json: String): String {
        return try {
            org.json.JSONObject(json).optString("text", "")
        } catch (e: Exception) {
            ""
        }
    }
}
