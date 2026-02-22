package com.navarro.voice

import android.content.Context
import android.media.AudioAttributes
import android.speech.tts.TextToSpeech
import com.navarro.core.Logger
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

class TextToSpeechManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private val ready = AtomicBoolean(false)
    private val appContext = context.applicationContext

    private var onDone: (() -> Unit)? = null

    init {
        tts = TextToSpeech(appContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {

            val result = tts?.setLanguage(Locale.FRENCH)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Logger.e("TTS langue FR non supportée")
                return
            }

            tts?.setPitch(1.0f)
            tts?.setSpeechRate(1.0f)

            tts?.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )

            tts?.setOnUtteranceProgressListener(object :
                android.speech.tts.UtteranceProgressListener() {

                override fun onStart(utteranceId: String?) {
                    Logger.i("TTS start")
                }

                override fun onDone(utteranceId: String?) {
                    Logger.i("TTS done")
                    onDone?.invoke()
                }

                override fun onError(utteranceId: String?) {
                    Logger.e("TTS error")
                    onDone?.invoke()
                }
            })

            ready.set(true)
            Logger.i("TTS prêt")

        } else {
            Logger.e("TTS init échoué")
        }
    }

    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        if (!ready.get()) {
            Logger.w("TTS non prêt")
            return
        }

        onDone = onComplete

        tts?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "NAVARRO_TTS_${System.currentTimeMillis()}"
        )
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.shutdown()
        tts = null
        ready.set(false)
    }
}
