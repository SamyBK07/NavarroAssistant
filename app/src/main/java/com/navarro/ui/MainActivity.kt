package com.navarro.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.navarro.R
import com.navarro.hotword.HotwordService
import com.navarro.voice.TextToSpeechManager

class MainActivity : AppCompatActivity() {

    private val REQUEST_PERMISSIONS = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TextToSpeechManager.init(this)

        checkPermissions()
    }

    private fun checkPermissions() {

        val permissionsNeeded = mutableListOf(
            Manifest.permission.RECORD_AUDIO
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val notGranted = permissionsNeeded.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                notGranted.toTypedArray(),
                REQUEST_PERMISSIONS
            )
        } else {
            startHotwordService()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSIONS &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        ) {
            startHotwordService()
        } else {
            Toast.makeText(
                this,
                "Permissions requises pour fonctionner",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    private fun startHotwordService() {
        val intent = Intent(this, HotwordService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    override fun onDestroy() {
        TextToSpeechManager.shutdown()
        super.onDestroy()
    }
}
