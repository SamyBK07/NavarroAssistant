package com.navarro.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.navarro.core.Logger
import java.util.Locale

class TextToSpeechManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isReady = false
    private val appContext = context.applicationContext

    init {
        tts = TextToSpeech(appContext, this)
    }

    override fun onInit(status: Int) {
        isReady = status == TextToSpeech.SUCCESS
        if (isReady) {
            val result = tts?.setLanguage(Locale.FRENCH)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Logger.e("TTS: Language not supported")
                isReady = false
            } else {
                Logger.i("TTS initialized and ready")
            }
        } else {
            Logger.e("TTS initialization failed")
        }
    }

    fun speak(text: String) {
        if (!isReady) return
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "NAVARRO_TTS")
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.shutdown()
        tts = null
        isReady = false
    }
}
