package com.navarro.hotword

import android.content.Context
import com.navarro.core.Logger
import ai.picovoice.porcupine.PorcupineManager
import ai.picovoice.porcupine.PorcupineManagerCallback

class HotwordRecognizer(
    private val context: Context,
    private val onDetected: () -> Unit
) {

    private var porcupineManager: PorcupineManager? = null
    private var isListening = false

    fun startListening() {
        if (isListening) return

        try {
            val keywordPath = "navarro_android.ppn" // placé dans assets/

            porcupineManager = PorcupineManager.Builder()
                .setAccessKey("4Goo3OCmV2nSDzSeQ5xj6uEJaV2+aFLFmi3QIUtacO1FB9ToOahCsA==")
                .setKeywordPath(keywordPath)
                .setSensitivity(0.7f)
                .build(
                    context,
                    PorcupineManagerCallback { keywordIndex ->
                        Logger.d("Wake word détecté via Porcupine")
                        onDetected()
                    }
                )

            porcupineManager?.start()
            isListening = true
            Logger.d("HotwordRecognizer Picovoice démarré")

        } catch (e: Exception) {
            Logger.e("HotwordRecognizer Picovoice error: ${e.message}")
        }
    }

    fun stopListening() {
        try {
            porcupineManager?.stop()
            porcupineManager?.delete()
            porcupineManager = null
            isListening = false
            Logger.d("HotwordRecognizer Picovoice arrêté")
        } catch (e: Exception) {
            Logger.e("Stop error: ${e.message}")
        }
    }
}
