package com.navarro.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import com.navarro.core.Logger
import java.util.Locale

object TextToSpeechManager : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    fun init(context: Context) {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.FRENCH)
            isInitialized = result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_AVAILABLE
            Logger.d("TTS initialisé: $isInitialized")
        } else {
            Logger.e("Erreur initialisation TTS")
        }
    }

    fun speak(text: String) {
        if (isInitialized) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Logger.e("TTS non initialisé")
        }
    }

    fun shutdown() {
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
