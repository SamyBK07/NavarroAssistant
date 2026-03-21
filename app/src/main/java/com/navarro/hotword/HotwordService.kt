package com.navarro.hotword

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.navarro.core.Logger

class HotwordService : Service() {

    private var voskRecognizer: VoskRecognizer? = null
    private var isCommandMode = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(1, NotificationHelper.createNotification(this))
        startHotwordMode()
    }

    private fun startHotwordMode() {
        voskRecognizer = VoskRecognizer(this, { result ->
            try {
                if (result.contains("navarro", ignoreCase = true)) {
                    Logger.d("Mot-clé détecté → Passage en mode commande")
                    voskRecognizer?.stopListening()
                    startCommandMode()
                }
            } catch (e: Exception) {
                Logger.e("Erreur de traitement du résultat Vosk (mot-clé): ${e.message}")
            }
        }, isHotwordMode = true)

        voskRecognizer?.startListening()
        Logger.d("HotwordService démarré en mode mot-clé")
    }

    private fun startCommandMode() {
        voskRecognizer = VoskRecognizer(this, { command ->
            Logger.d("Commande reconnue: $command")
            // Traiter la commande ici (ex: envoyer à un gestionnaire de commandes)
            onCommandRecognized(command)

            // Retour automatique au mode mot-clé après 5 secondes d'inactivité
            Handler(Looper.getMainLooper()).postDelayed({
                voskRecognizer?.stopListening()
                startHotwordMode()
            }, 5000)
        }, isHotwordMode = false)

        voskRecognizer?.startListening()
        isCommandMode = true
        Logger.d("HotwordService basculé en mode commande")
    }

    private fun onCommandRecognized(command: String) {
        // Logique pour traiter la commande (ex: Intent, API, etc.)
        Logger.d("Traitement de la commande: $command")
        // Exemple: envoyer la commande à un autre composant
    }

    override fun onDestroy() {
        voskRecognizer?.stopListening()
        Logger.d("HotwordService arrêté")
        super.onDestroy()
    }
}
