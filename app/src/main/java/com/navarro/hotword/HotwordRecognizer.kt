package com.navarro.hotword

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.navarro.core.Logger
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.SpeechService
import java.io.File
import java.io.FileOutputStream

class VoskRecognizer(
    private val context: Context,
    private val onResult: (String) -> Unit
) {
    private var model: Model? = null
    private var speechService: SpeechService? = null
    @Volatile private var isListening = false

    fun startListening() {
        if (isListening) {
            Logger.w("Vosk est déjà en écoute")
            return
        }

        Thread {
            try {
                val modelPath = copyAssetFolder("vosk-model-small-fr") // Vérifie le nom exact dans assets
                model = Model(modelPath.absolutePath)
                val recognizer = Recognizer(model, 16000.0f)

                speechService = SpeechService(recognizer, 16000.0f)
                speechService?.startListening { result ->
                    Handler(Looper.getMainLooper()).post {
                        Logger.d("Résultat Vosk: $result")
                        onResult(result)
                    }
                }

                isListening = true
                Logger.d("Vosk démarré avec succès")

            } catch (e: Exception) {
                Logger.e("Échec du démarrage de Vosk: ${e.message}")
                Handler(Looper.getMainLooper()).post {
                    onResult("{\"error\": \"Échec de l'initialisation de Vosk\"}")
                }
            }
        }.start()
    }

    fun stopListening() {
        if (!isListening) return

        try {
            speechService?.stop()
            speechService?.shutdown()
            model?.close()
        } catch (e: Exception) {
            Logger.e("Erreur lors de l'arrêt de Vosk: ${e.message}")
        } finally {
            speechService = null
            model = null
            isListening = false
        }
    }

    private fun copyAssetFolder(assetName: String): File {
        val outDir = File(context.filesDir, assetName)
        if (outDir.exists()) return outDir

        try {
            copyAssetsRecursive(assetName, outDir)
            return outDir
        } catch (e: Exception) {
            Logger.e("Échec de la copie du modèle Vosk: ${e.message}")
            throw RuntimeException("Modèle Vosk introuvable dans assets/$assetName")
        }
    }

    private fun copyAssetsRecursive(path: String, outFile: File) {
        val assets = context.assets.list(path) ?: throw RuntimeException("Dossier $path introuvable dans assets")

        if (assets.isEmpty()) {
            // Fichier
            context.assets.open(path).use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            }
        } else {
            // Dossier
            outFile.mkdirs()
            assets.forEach { file ->
                copyAssetsRecursive("$path/$file", File(outFile, file))
            }
        }
    }
}
