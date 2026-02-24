package com.navarro.core

import android.content.Context
import java.io.File

object AppConfig {
    const val MISTRAL_API_KEY = "PD85t8aUMKTkZGDlAWhOWyxywwYRkSq1"
    const val VOSK_MODEL_URL = "https://alphacephei.com/vosk/models/vosk-model-small-fr-0.22.zip"

    fun getVoskModelDir(context: Context): File {  // ✅ Accepte un Context
        return File(context.filesDir, "models")
    }
}
