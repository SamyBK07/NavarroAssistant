package com.navarro.core

import android.app.Application
import java.io.File

object AppConfig {
    // 🔑 Clé API Mistral (à remplacer par ta clé réelle)
    const val MISTRAL_API_KEY = "PD85t8aUMKTkZGDlAWhOWyxywwYRkSq1" // Ex: "x123456789..."

    // 📥 URL du modèle VOSK (modèle français léger)
    const val VOSK_MODEL_URL = "https://alphacephei.com/vosk/models/vosk-model-small-fr-0.22.zip"

    // 📁 Dossier de stockage du modèle VOSK
    fun getVoskModelDir(context: Application): File {
        return File(context.filesDir, "models")
    }
}
