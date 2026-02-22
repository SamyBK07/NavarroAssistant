package com.navarro.hotword

import android.content.Context
import android.util.Log
import com.navarro.core.Logger
import java.io.*
import java.net.URL
import java.util.zip.ZipInputStream

class ModelDownloader(private val context: Context) {

    private val modelUrl =
        "https://alphacephei.com/vosk/models/vosk-model-small-fr-0.22.zip"

    private val modelDir = File(context.filesDir, "vosk-model")

    fun isModelPresent(): Boolean {
        return modelDir.exists() && modelDir.list()?.isNotEmpty() == true
    }

    fun downloadModel(onComplete: () -> Unit, onError: (Exception) -> Unit) {
        Thread {
            try {
                Logger.i("ModelDownloader: téléchargement modèle VOSK")

                val zipFile = File(context.cacheDir, "model.zip")
                URL(modelUrl).openStream().use { input ->
                    FileOutputStream(zipFile).use { output ->
                        input.copyTo(output)
                    }
                }

                unzip(zipFile, modelDir)
                zipFile.delete()

                Logger.i("ModelDownloader: modèle prêt")
                onComplete()

            } catch (e: Exception) {
                Logger.e("ModelDownloader erreur: ${e.message}")
                onError(e)
            }
        }.start()
    }

    private fun unzip(zipFile: File, targetDir: File) {
        ZipInputStream(FileInputStream(zipFile)).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val newFile = File(targetDir, entry.name)

                if (entry.isDirectory) {
                    newFile.mkdirs()
                } else {
                    newFile.parentFile?.mkdirs()
                    FileOutputStream(newFile).use { fos ->
                        zis.copyTo(fos)
                    }
                }

                entry = zis.nextEntry
            }
            zis.closeEntry()
        }
    }

    fun getModelPath(): String = modelDir.absolutePath
}
