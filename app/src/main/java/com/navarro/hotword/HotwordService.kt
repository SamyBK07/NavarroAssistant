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

    private val MODEL_URL =
        "https://alphacephei.com/vosk/models/vosk-model-small-fr-0.22.zip"

    private enum class State { PASSIVE, ACTIVE }
    private var currentState = State.PASSIVE

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())

        tts = TextToSpeech(this) {
            tts.language = Locale.FRANCE
        }

        Thread {
            try {
                val modelDir = File(filesDir, "vosk-model-small-fr-0.22")

                if (!modelDir.exists()) {
                    downloadAndUnzip(MODEL_URL, filesDir)
                }

                model = Model(modelDir.absolutePath)
                startPassiveListening()

            } catch (e: Exception) {
                Log.e("NAVARRO_ERROR", e.message ?: "Erreur init")
            }
        }.start()
    }

    // ================= PASSIVE MODE =================

    private fun startPassiveListening() {

        currentState = State.PASSIVE

        val recognizer = Recognizer(model, 16000.0f, "[\"navarro\"]")
        speechService = SpeechService(recognizer, 16000.0f)

        speechService?.startListening(object : RecognitionListener {

            override fun onPartialResult(hypothesis: String?) {
                if (hypothesis?.contains("navarro", true) == true) {
                    speechService?.stop()
                    onWakeWordDetected()
                }
            }

            override fun onResult(hypothesis: String?) {}
            override fun onFinalResult(hypothesis: String?) {}
            override fun onError(e: Exception?) {}
            override fun onTimeout() {}
        })

        Log.d("NAVARRO", "Mode PASSIF")
    }

    // ================= WAKE WORD =================

    private fun onWakeWordDetected() {
        speak("Oui monsieur") {
            startActiveListening()
        }
    }

    // ================= ACTIVE MODE =================

    private fun startActiveListening() {

        currentState = State.ACTIVE

        val recognizer = Recognizer(model, 16000.0f)
        speechService = SpeechService(recognizer, 16000.0f)

        speechService?.startListening(object : RecognitionListener {

            override fun onResult(hypothesis: String?) {

                val text = hypothesis
                    ?.substringAfter("\"text\":\"")
                    ?.substringBefore("\"")
                    ?: ""

                if (text.isNotBlank()) {
                    speechService?.stop()
                    handleCommand(text)
                }
            }

            override fun onPartialResult(hypothesis: String?) {}
            override fun onFinalResult(hypothesis: String?) {}
            override fun onError(e: Exception?) {}
            override fun onTimeout() {}
        })

        // Timeout 15 secondes
        Handler(mainLooper).postDelayed({
            if (currentState == State.ACTIVE) {
                speechService?.stop()
                startPassiveListening()
            }
        }, 15000)

        Log.d("NAVARRO", "Mode ACTIF")
    }

    // ================= COMMAND HANDLER =================

    private fun handleCommand(text: String) {

        when {

            text.contains("heure", true) -> {
                val time = SimpleDateFormat("HH:mm", Locale.FRANCE)
                    .format(Date())

                speak("Il est $time") {
                    startPassiveListening()
                }
            }

            text.contains("ouvre youtube", true) -> {
                val intent =
                    packageManager.getLaunchIntentForPackage("com.google.android.youtube")
                intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent?.let { startActivity(it) }

                speak("J'ouvre Youtube") {
                    startPassiveListening()
                }
            }

            else -> {
                SmartApi.send(this, text) { response ->
                    speak(response) {
                        startPassiveListening()
                    }
                }
            }
        }
    }

    // ================= TEXT TO SPEECH =================

    private fun speak(text: String, onDone: (() -> Unit)? = null) {

        val utteranceId = "NAVARRO_TTS"

        tts.setOnUtteranceProgressListener(
            object : android.speech.tts.UtteranceProgressListener() {

                override fun onStart(p0: String?) {}

                override fun onDone(p0: String?) {
                    Handler(mainLooper).post {
                        onDone?.invoke()
                    }
                }

                override fun onError(p0: String?) {}
            })

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    // ================= MODEL DOWNLOAD =================

    private fun downloadAndUnzip(urlStr: String, destDir: File) {

        val zipFile = File(destDir, "model.zip")

        URL(urlStr).openStream().use { input ->
            FileOutputStream(zipFile).use { output ->
                input.copyTo(output)
            }
        }

        ZipInputStream(FileInputStream(zipFile)).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                val file = File(destDir, entry.name)
                if (entry.isDirectory) {
                    file.mkdirs()
                } else {
                    file.parentFile?.mkdirs()
                    FileOutputStream(file).use { zip.copyTo(it) }
                }
                entry = zip.nextEntry
            }
        }

        zipFile.delete()
    }

    override fun onDestroy() {
        speechService?.stop()
        tts.shutdown()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {

        val channelId = "navarro_channel"

        val channel = NotificationChannel(
            channelId,
            "Navarro Service",
            NotificationManager.IMPORTANCE_LOW
        )

        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Navarro actif")
            .setContentText("Écoute en cours...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()
    }
}
