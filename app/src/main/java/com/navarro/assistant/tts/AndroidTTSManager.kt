package com.navarro.assistant.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class AndroidTTSManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.FRENCH)
            isReady = result != TextToSpeech.LANG_MISSING_DATA &&
                      result != TextToSpeech.LANG_NOT_SUPPORTED

            Log.d("Navarro-TTS", "TTS prêt : $isReady")
        } else {
            Log.e("Navarro-TTS", "Échec initialisation TTS")
        }
    }

    fun speak(text: String) {
        if (!isReady) {
            Log.e("Navarro-TTS", "TTS non prêt")
            return
        }

        Log.d("Navarro-TTS", "Lecture : $text")

        tts?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "NAVARRO_TTS_ID"
        )
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        Log.d("Navarro-TTS", "TTS arrêté")
    }
}
