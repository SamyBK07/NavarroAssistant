package com.navarro.assistant.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.navarro.assistant.R
import com.navarro.assistant.system.ForegroundService

class MainActivity : AppCompatActivity() {

    private lateinit var logView: TextView
    private lateinit var statusView: TextView
    private lateinit var startButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logView = findViewById(R.id.logView)
        statusView = findViewById(R.id.statusView)
        startButton = findViewById(R.id.startButton)

        // Initialisation de l'état des ressources
        updateStatusChecklist("Microphone", "OK")
        updateStatusChecklist("TTS", "OK")
        updateStatusChecklist("Bluetooth", "OK")
        updateStatusChecklist("Wi-Fi", "OK")
        updateStatusChecklist("Internet", "OK")

        // Bouton pour démarrer le ForegroundService
        startButton.setOnClickListener {
            appendLog("Démarrage du service de surveillance...")
            val serviceIntent = Intent(this, ForegroundService::class.java)
            startForegroundService(serviceIntent)
        }
    }

    // Ajoute une ligne dans la zone de logs
    fun appendLog(message: String) {
        logView.append("$message\n")
    }

    // Met à jour la checklist des ressources
    fun updateStatusChecklist(resource: String, status: String) {
        val currentText = statusView.text.toString()
        statusView.text = "$currentText$resource : $status\n"
    }
}
