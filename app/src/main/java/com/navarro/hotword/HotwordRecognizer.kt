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
            Logger.w("Vosk déjà en écoute (${mode()})")
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
                        Logger.d("Partiel: $result")
                    }

                    override fun onResult(result: String) {
                        Handler(Looper.getMainLooper()).post {
                            Logger.d("Résultat (${mode()}): $result")
                            onResult(result)
                        }
                    }

                    override fun onFinalResult(result: String) {
                        Logger.d("Final: $result")
                    }

                    override fun onError(exception: Exception) {
                        Logger.e("Erreur Vosk: ${exception.message}")
                        Handler(Looper.getMainLooper()).post {
                            onResult("ERROR: ${exception.message}")
                        }
                    }

                    override fun onTimeout() {
                        Logger.w("Timeout Vosk")
                        Handler(Looper.getMainLooper()).post {
                            onResult("TIMEOUT")
                        }
                    }
                })

                isListening = true
                Logger.d("Vosk démarré (${mode()})")

            } catch (e: Exception) {
                Logger.e("Échec Vosk (${mode()}): ${e.message}")
                Handler(Looper.getMainLooper()).post {
                    onResult("ERROR: INIT FAILED")
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
            Logger.e("Erreur arrêt Vosk: ${e.message}")
        } finally {
            speechService = null
            model = null
            isListening = false
            Logger.d("Vosk arrêté (${mode()})")
        }
    }

    private fun mode(): String {
        return if (isHotwordMode) "mot-clé" else "commande"
    }

    private fun copyAssetFolder(assetName: String): File {
        val outDir = File(context.filesDir, assetName)
        if (outDir.exists()) return outDir

        try {
            copyAssetsRecursive(assetName, outDir)
            return outDir
        } catch (e: Exception) {
            Logger.e("Erreur copie modèle: ${e.message}")
            throw RuntimeException("Modèle Vosk introuvable: assets/$assetName")
        }
    }

    private fun copyAssetsRecursive(path: String, outFile: File) {
        val assets = context.assets.list(path)
            ?: throw RuntimeException("Dossier introuvable: $path")

        if (assets.isEmpty()) {
            outFile.parentFile?.mkdirs()
            context.assets.open(path).use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            }
        } else {
            outFile.mkdirs()
            assets.forEach { file ->
                copyAssetsRecursive("$path/$file", File(outFile, file))
            }
        }
    }
}
