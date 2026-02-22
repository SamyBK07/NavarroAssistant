package com.navarro.hotword

import android.content.Context
import com.navarro.core.Logger
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class HotwordRecognizer(
    private val context: Context,
    private val modelPath: String,
    private val onHotwordDetected: () -> Unit
) : RecognitionListener {

    private var model: Model? = null
    private var recognizer: Recognizer? = null
    private var speechService: SpeechService? = null

    private val sampleRate = 16000f
    private val detectionLock = AtomicBoolean(false)

    fun start() {
        try {
            val modelDir = File(modelPath)

            if (!modelDir.exists()) {
                Logger.e("Modèle VOSK introuvable: $modelPath")
                return
            }

            Logger.i("Chargement modèle VOSK...")
            model = Model(modelDir.absolutePath)

            val grammar = "[\"navarro\", \"[unk]\"]"
            recognizer = Recognizer(model, sampleRate, grammar)

            speechService = SpeechService(recognizer, sampleRate)
            speechService?.startListening(this)

            Logger.i("Hotword écoute active")

        } catch (e: Exception) {
            Logger.e("Erreur start hotword", e)
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
            detectionLock.set(false)

            Logger.i("Hotword arrêté")

        } catch (e: Exception) {
            Logger.e("Erreur stop hotword", e)
        }
    }

    override fun onPartialResult(hypothesis: String?) {
        val text = extractText(hypothesis) ?: return

        Logger.d("Partial: $text")

        if (text.contains("navarro", true) && detectionLock.compareAndSet(false, true)) {
            Logger.i("Hotword détecté")
            onHotwordDetected.invoke()
        }
    }

    override fun onResult(hypothesis: String?) {}
    override fun onFinalResult(hypothesis: String?) {}

    override fun onError(e: Exception?) {
        Logger.e("Hotword error", e)
    }

    override fun onTimeout() {
        Logger.w("Hotword timeout")
    }

    private fun extractText(json: String?): String? {
        if (json == null) return null

        return try {
            val obj = JSONObject(json)
            obj.optString("partial", obj.optString("text", ""))
        } catch (e: Exception) {
            null
        }
    }
}
