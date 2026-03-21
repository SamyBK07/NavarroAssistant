package com.navarro.hotword

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.navarro.core.Logger
import org.json.JSONObject
import org.vosk.LibVosk
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.SpeechStreamService
import org.vosk.android.StorageService

class HotwordService : Service() {

    private lateinit var voskRecognizer: VoskRecognizer

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        // Créer la notification en premier (obligatoire pour Android 8+)
        startForeground(1, NotificationHelper.createNotification(this))

        try {
            // Charger le modèle Vosk depuis assets
            val model = Model(StorageService.unpack(this, "vosk-model", "model"))
            val recognizer = Recognizer(model, 16000.0f)

            voskRecognizer = VoskRecognizer(this, recognizer) { result ->
                try {
                    val text = JSONObject(result).optString("text")
                    if (text.contains("navarro", ignoreCase = true)) {
                        Logger.d("Mot clé détecté via Vosk")
                        voskRecognizer.stopListening()

                        // Démarrer le service de commande vocale
                        val intent = Intent(this, com.navarro.voice.VoiceCommandService::class.java)
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            startForegroundService(intent)
                        } else {
                            startService(intent)
                        }
                    }
                } catch (e: Exception) {
                    Logger.e("Erreur de parsing Vosk : ${e.message}")
                }
            }

            voskRecognizer.startListening()
            Logger.d("HotwordService Vosk démarré avec succès")

        } catch (e: Exception) {
            Logger.e("Échec de l'initialisation de Vosk : ${e.message}")
            stopSelf() // Arrêter le service si Vosk ne démarre pas
        }
    }

    override fun onDestroy() {
        if (::voskRecognizer.isInitialized) {
            voskRecognizer.stopListening()
        }
        Logger.d("HotwordService arrêté")
        super.onDestroy()
    }
}
