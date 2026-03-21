package com.navarro.hotword

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.navarro.core.Logger
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import java.io.File
import java.io.FileOutputStream

class VoskRecognizer(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val isHotwordMode: Boolean = true
) {
    private var model: Model? = null
    private var speechService: SpeechService? = null
    @Volatile private var isListening = false

    fun startListening() {
        if (isListening) {
            Logger.w("Vosk est déjà en écoute (mode ${if (isHotwordMode) "mot-clé" else "commande"})")
            return
        }

        Thread {
            try {
                val modelPath = copyAssetFolder("vosk-model-small-fr")
                model = Model(modelPath.absolutePath)
                val recognizer = Recognizer(model, 16000.0f)

                speechService = SpeechService(recognizer, 16000.0f)
                speechService?.startListening(object : RecognitionListener {
                    override fun onPartialResult(result: String) {
                        Logger.d("Résultat partiel Vosk: $result")
                    }

                    override fun onResult(result: String) {
                        Handler(Looper.getMainLooper()).post {
                            Logger.d("Résultat Vosk (${if (isHotwordMode) "mot-clé" else "commande"}): $result")
                            onResult(result)
                        }
                    }

                    override fun onFinalResult(result: String) {
                        Logger.d("Résultat final Vosk: $result")
                    }

                    override fun onError(exception: Exception) {
                        Logger.e("Erreur Vosk: ${exception.message}")
                        Handler(Looper.getMainLooper()).post {
                            onResult("{\"error\": \"Erreur de reconnaissance: ${exception.message}\"}")
                        }
                    }

                    override fun onTimeout() {
                        Logger.w("Timeout Vosk")
                        Handler(Looper.getMainLooper()).post {
                            onResult("{\"error\": \"Timeout de reconnaissance\"}")
                        }
                    }
                })

                isListening = true
                Logger.d("Vosk démarré en mode ${if (isHotwordMode) "mot-clé" else "commande"}")

            } catch (e: Exception) {
                Logger.e("Échec du démarrage de Vosk (${if (isHotwordMode) "mot-clé" else "commande"}): ${e.message}")
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
            Logger.d("Vosk arrêté (mode ${if (isHotwordMode) "mot-clé" else "commande"})")
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
        val assets = context.assets.list(path)
            ?: throw RuntimeException("Dossier $path introuvable dans assets")

        if (assets.isEmpty()) {
            // Fichier
            outFile.parentFile?.mkdirs()
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
