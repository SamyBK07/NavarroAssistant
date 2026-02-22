package com.navarro.hotword

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import com.navarro.core.AppConfig
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

object ModelDownloader {

    /**
     * Télécharge et extrait le modèle VOSK.
     * @param context Contexte Android.
     * @param onSuccess Callback en cas de succès.
     * @param onError Callback en cas d'erreur.
     */
    fun downloadVoskModel(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val modelDir = AppConfig.getVoskModelDir(context.applicationContext)
        if (modelDir.exists() && modelDir.listFiles().isNotEmpty()) {
            onSuccess()
            return
        }

        val request = DownloadManager.Request(Uri.parse(AppConfig.VOSK_MODEL_URL))
            .setTitle("Téléchargement du modèle vocal")
            .setDescription("Nécessaire pour la reconnaissance vocale")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "vosk-model.zip")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                if (id == downloadId) {
                    context.unregisterReceiver(this)
                    val downloadFile = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "vosk-model.zip"
                    )
                    try {
                        unzipModel(downloadFile, modelDir)
                        downloadFile.delete() // Supprime le ZIP après extraction
                        onSuccess()
                    } catch (e: Exception) {
                        onError("Échec de l'extraction : ${e.message}")
                    }
                }
            }
        }

        context.registerReceiver(
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    /**
     * Extrait un fichier ZIP dans un dossier.
     * @param zipFile Fichier ZIP à extraire.
     * @param outputDir Dossier de destination.
     */
    private fun unzipModel(zipFile: File, outputDir: File) {
        outputDir.mkdirs()
        ZipInputStream(FileInputStream(zipFile)).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                val outputFile = File(outputDir, entry.name)
                if (entry.isDirectory) {
                    outputFile.mkdirs()
                } else {
                    outputFile.parentFile?.mkdirs()
                    FileOutputStream(outputFile).use { output ->
                        zip.copyTo(output)
                    }
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
    }
}
