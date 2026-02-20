package com.navarro.hotword

import android.app.*
import android.content.Intent
import android.os.*
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.NotificationCompat
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.SpeechService
import org.vosk.android.RecognitionListener
import java.io.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipInputStream

class HotwordService : Service() {

    private var speechService: SpeechService? = null
    private lateinit var tts: TextToSpeech
    private lateinit var model: Model
    private var activeTimer: CountDownTimer? = null

    private val MODEL_URL = "https://alphacephei.com/vosk/models/vosk-model-small-fr-0.22.zip"

    private enum class State { PASSIVE, ACTIVE }
    private var currentState = State.PASSIVE

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())

        // Initialisation du TTS
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.FRANCE
                Log.d("NAVARRO", "TTS initialisé")
            } else {
                Log.e("NAVARRO_ERROR", "Échec TTS")
            }
        }

        // Chargement du modèle VOSK
        Thread {
            try {
                val modelDir = File(filesDir, "vosk-model-small-fr-0.22")
                if (!modelDir.exists() || modelDir.listFiles()?.isEmpty() == true) {
                    downloadAndUnzip(MODEL_URL, filesDir)
                }
                model = Model(modelDir.absolutePath)
                startPassiveListening()
            } catch (e: Exception) {
                Log.e("NAVARRO_ERROR", "Erreur modèle: ${e.message}")
            }
        }.start()
    }

    // ================= MODE PASSIF =================
    private fun startPassiveListening() {
        currentState = State.PASSIVE
        try {
            val recognizer = Recognizer(model, 16000.0f, "[\"navarro\"]")
            speechService = SpeechService(recognizer, 16000.0f)

            speechService?.startListening(object : RecognitionListener {
                override fun onPartialResult(hypothesis: String?) {
                    if (hypothesis?.contains("navarro", true) == true) {
                        Handler(mainLooper).post {
                            speechService?.stop()
                            speak("Un instant") {
                                startActiveListening()
                            }
                        }
                    }
                }
                override fun onResult(hypothesis: String?) {}
                override fun onFinalResult(hypothesis: String?) {}
                override fun onError(e: Exception?) {
                    Log.e("NAVARRO_ERROR", "Erreur mode passif: ${e?.message}")
                    startPassiveListening() // Relance
                }
                override fun onTimeout() {}
            })
            Log.d("NAVARRO", "Mode PASSIF activé")
        } catch (e: Exception) {
            Log.e("NAVARRO_ERROR", "Démarrage mode passif échoué: ${e.message}")
        }
    }

    // ================= MODE ACTIF =================
    private fun startActiveListening() {
        currentState = State.ACTIVE
        try {
            val recognizer = Recognizer(model, 16000.0f)
            speechService = SpeechService(recognizer, 16000.0f)

            speechService?.startListening(object : RecognitionListener {
                override fun onResult(hypothesis: String?) {
                    val text = hypothesis
                        ?.substringAfter("\"text\":\"")
                        ?.substringBefore("\"")
                        ?.lowercase(Locale.FRANCE)
                        ?: ""
                    if (text.isNotBlank()) {
                        Handler(mainLooper).post {
                            speechService?.stop()
                            handleCommand(text)
                        }
                    }
                }
                override fun onPartialResult(hypothesis: String?) {}
                override fun onFinalResult(hypothesis: String?) {}
                override fun onError(e: Exception?) {
                    Log.e("NAVARRO_ERROR", "Erreur mode actif: ${e?.message}")
                }
                override fun onTimeout() {}
            })

            // Timeout 15 secondes
            activeTimer = object : CountDownTimer(15000, 1000) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    if (currentState == State.ACTIVE) {
                        speechService?.stop()
                        startPassiveListening()
                    }
                }
            }.start()
            Log.d("NAVARRO", "Mode ACTIF activé")
        } catch (e: Exception) {
            Log.e("NAVARRO_ERROR", "Démarrage mode actif échoué: ${e.message}")
        }
    }

    // ================= GESTION DES COMMANDES =================
    private fun handleCommand(text: String) {
        CommandManager.executeCommand(text) { response ->
            sendResponseToActivity(response)
            startPassiveListening()
        }
    }

    // ================= ENVOI DE LA RÉPONSE À MAINACTIVITY =================
    private fun sendResponseToActivity(response: String) {
        val intent = Intent("com.navarro.RESPONSE").apply {
            putExtra("response", response)
        }
        sendBroadcast(intent)
        speak(response)
    }

    // ================= SYNTHÈSE VOCALE =================
    private fun speak(text: String, onDone: (() -> Unit)? = null) {
        val utteranceId = UUID.randomUUID().toString()
        tts.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                Handler(mainLooper).post { onDone?.invoke() }
            }
            override fun onError(utteranceId: String?) {
                Log.e("NAVARRO_ERROR", "Erreur TTS")
            }
        })
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    // ================= TÉLÉCHARGEMENT DU MODÈLE =================
    private fun downloadAndUnzip(urlStr: String, destDir: File) {
        try {
            val modelDir = File(destDir, "vosk-model-small-fr-0.22")
            val zipFile = File(destDir, "model.zip")
            URL(urlStr).openStream().use { input ->
                FileOutputStream(zipFile).use { output ->
                    input.copyTo(output)
                }
            }
            ZipInputStream(FileInputStream(zipFile)).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    val file = File(modelDir, entry.name)
                    if (entry.isDirectory) file.mkdirs()
                    else {
                        file.parentFile?.mkdirs()
                        FileOutputStream(file).use { zip.copyTo(it) }
                    }
                    entry = zip.nextEntry
                }
            }
            zipFile.delete()
            Log.d("NAVARRO", "Modèle téléchargé")
        } catch (e: Exception) {
            Log.e("NAVARRO_ERROR", "Téléchargement échoué: ${e.message}")
            throw e
        }
    }

    // ================= NOTIFICATION =================
    private fun createNotification(): Notification {
        val channelId = "navarro_channel"
        val channel = NotificationChannel(
            channelId,
            "Navarro Service",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Navarro actif")
            .setContentText("Écoute en cours...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()
    }

    override fun onDestroy() {
        activeTimer?.cancel()
        speechService?.stop()
        tts.shutdown()
        super.onDestroy()
        Log.d("NAVARRO", "Service arrêté")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
