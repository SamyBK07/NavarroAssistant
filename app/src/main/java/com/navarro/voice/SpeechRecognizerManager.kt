package com.navarro.voice

import android.content.Context
import com.navarro.core.Logger
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class SpeechRecognizerManager(
    private val context: Context,
    private val modelPath: String,
    private val onResult: (String) -> Unit,
    private val onError: (() -> Unit)? = null
) : RecognitionListener {

    private var model: Model? = null
    private var recognizer: Recognizer? = null
    private var speechService: SpeechService? = null

    private val sampleRate = 16000f
    private val resultSent = AtomicBoolean(false)
    private val buffer = StringBuilder()

    fun start() {
        try {
            val modelDir = File(modelPath)

            if (!modelDir.exists()) {
                Logger.e("Modèle VOSK introuvable pour commande")
                onError?.invoke()
                return
            }

            Logger.i("Chargement modèle commande...")
            model = Model(modelDir.absolutePath)

            recognizer = Recognizer(model, sampleRate)
            speechService = SpeechService(recognizer, sampleRate)
            speechService?.startListening(this)

            Logger.i("Écoute commande active")

        } catch (e: Exception) {
            Logger.e("Erreur start commande", e)
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
            buffer.clear()
            resultSent.set(false)

            Logger.i("Commande arrêtée")

        } catch (e: Exception) {
            Logger.e("Erreur stop commande", e)
        }
    }

    override fun onPartialResult(hypothesis: String?) {
        // utile debug UI si besoin
    }

    override fun onResult(hypothesis: String?) {
        val text = extractText(hypothesis)
        if (text.isNotEmpty()) {
            buffer.append(" ").append(text)
        }
    }

    override fun onFinalResult(hypothesis: String?) {
        val text = extractText(hypothesis)

        if (!resultSent.getAndSet(true)) {
            val finalText = (buffer.toString() + " " + text).trim()

            if (finalText.isNotEmpty()) {
                Logger.i("Commande finale: $finalText")
                onResult.invoke(finalText)
            } else {
                onError?.invoke()
            }
        }
    }

    override fun onError(e: Exception?) {
        Logger.e("Erreur reconnaissance commande", e)
        onError?.invoke()
    }

    override fun onTimeout() {
        Logger.w("Timeout reconnaissance commande")

        if (!resultSent.getAndSet(true)) {
            val finalText = buffer.toString().trim()

            if (finalText.isNotEmpty()) {
                onResult.invoke(finalText)
            } else {
                onError?.invoke()
            }
        }
    }

    private fun extractText(json: String?): String {
        if (json == null) return ""

        return try {
            JSONObject(json).optString("text", "")
        } catch (e: Exception) {
            ""
        }
    }
}
