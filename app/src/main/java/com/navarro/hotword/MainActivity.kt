// MainActivity.kt
package com.navarro

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.navarro.hotword.HotwordService
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var clockView: ClockView
    private lateinit var responseText: TextView
    private var downloadId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)
        clockView = findViewById(R.id.clockView)
        responseText = findViewById(R.id.responseText)

        // Vérifie si le modèle VOSK est déjà téléchargé
        if (!isModelDownloaded()) {
            downloadModel()
        } else {
            startHotwordService()
        }
    }

    private fun isModelDownloaded(): Boolean {
        val modelDir = File(filesDir, "vosk-model-small-fr-0.22")
        return modelDir.exists() && modelDir.listFiles()?.isNotEmpty() == true
    }

    private fun downloadModel() {
        progressBar.visibility = ProgressBar.VISIBLE
        val request = DownloadManager.Request(Uri.parse("https://alphacephei.com/vosk/models/vosk-model-small-fr-0.22.zip"))
            .setTitle("Téléchargement du modèle")
            .setDescription("Modèle VOSK pour la reconnaissance vocale")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "vosk-model.zip")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManager.enqueue(request)

        // Écouteur pour la fin du téléchargement
        registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private val onDownloadComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                unregisterReceiver(this)
                progressBar.visibility = ProgressBar.GONE
                clockView.visibility = ClockView.VISIBLE
                startHotwordService()
            }
        }
    }

    private fun startHotwordService() {
        startService(Intent(this, HotwordService::class.java))
    }

    // Affiche une réponse dans la zone de texte
    fun showResponse(text: String) {
        responseText.text = text
        responseText.visibility = TextView.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onDownloadComplete)
    }
}
