package com.navarro.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.navarro.R
import com.navarro.core.AppConfig
import com.navarro.hotword.HotwordService
import com.navarro.hotword.ModelDownloader
import com.navarro.voice.TextToSpeechManager

class MainActivity : AppCompatActivity() {
    private val REQUEST_RECORD_AUDIO = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialise le TTS
        TextToSpeechManager.init(this)

        // Vérifie les permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO
            )
        } else {
            checkAndDownloadVoskModel()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkAndDownloadVoskModel()
        } else {
            Toast.makeText(this, "Permission microphone requise", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun checkAndDownloadVoskModel() {
        ModelDownloader.downloadVoskModel(
            context = this,
            onSuccess = {
                runOnUiThread {
                    Toast.makeText(this, "Modèle VOSK prêt !", Toast.LENGTH_SHORT).show()
                    startHotwordService()
                }
            },
            onError = { error ->
                runOnUiThread {
                    Toast.makeText(this, "Erreur: $error", Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    private fun startHotwordService() {
        startService(Intent(this, HotwordService::class.java))
    }

    override fun onDestroy() {
        TextToSpeechManager.shutdown()
        super.onDestroy()
    }
}
