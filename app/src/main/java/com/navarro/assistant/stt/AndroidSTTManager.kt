package com.navarro.assistant.stt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

class AndroidSTTManager(private val context: Context) {

    private val speechRecognizer: SpeechRecognizer =
        SpeechRecognizer.createSpeechRecognizer(context)

    private val recognizerIntent: Intent =
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

    init {
        speechRecognizer.setRecognitionListener(listener)
    }

    fun startListening() {
        Log.d("Navarro-STT", "STT démarré")
        speechRecognizer.startListening(recognizerIntent)
    }

    fun stopListening() {
        Log.d("Navarro-STT", "STT arrêté")
        speechRecognizer.stopListening()
        speechRecognizer.destroy()
    }

    private val listener = object : RecognitionListener {

        override fun onReadyForSpeech(params: Bundle?) {
            Log.d("Navarro-STT", "Prêt à écouter")
        }

        override fun onBeginningOfSpeech() {
            Log.d("Navarro-STT", "Parole détectée")
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            Log.d("Navarro-STT", "Fin de parole")
        }

        override fun onError(error: Int) {
            Log.e("Navarro-STT", "Erreur STT : $error")
            restartListening()
        }

        override fun onResults(results: Bundle?) {
            val matches =
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

            if (!matches.isNullOrEmpty()) {
                val text = matches[0]
                Log.d("Navarro-STT", "Texte reconnu : $text")

                // 👉 ICI on enverra le texte à l’IA (plus tard)
            }

            restartListening()
        }

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun restartListening() {
        speechRecognizer.cancel()
        speechRecognizer.startListening(recognizerIntent)
    }
}
