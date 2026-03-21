package com.navarro.hotword

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.navarro.core.Logger
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.SpeechService
import org.vosk.android.SpeechStreamService
import java.io.File
import java.io.FileOutputStream

class VoskRecognizer(
    private val context: Context,
    private val onResult: (String) -> Unit
) {

    private var model: Model? = null
    private var speechService: SpeechService? = null
    private var isListening = false

    fun startListening() {
        if (isListening) return

        Thread {
            try {
                val modelPath = copyAssetFolder("vosk-model-small-fr")

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
                Logger.d("Vosk démarré")

            } catch (e: Exception) {
                Logger.e("Erreur Vosk: ${e.message}")
            }
        }.start()
    }

    fun stopListening() {
        try {
            speechService?.stop()
            speechService?.shutdown()
            model?.close()
        } catch (e: Exception) {
            Logger.e("Stop error: ${e.message}")
        } finally {
            speechService = null
            model = null
            isListening = false
        }
    }

    // 🔥 Copie dossier assets → filesDir
    private fun copyAssetFolder(assetName: String): File {
        val outDir = File(context.filesDir, assetName)

        if (outDir.exists()) return outDir

        copyAssetsRecursive(assetName, outDir)
        return outDir
    }

    private fun copyAssetsRecursive(path: String, outFile: File) {
        val assets = context.assets.list(path)

        if (assets.isNullOrEmpty()) {
            // fichier
            context.assets.open(path).use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            }
        } else {
            // dossier
            outFile.mkdirs()
            for (file in assets) {
                copyAssetsRecursive("$path/$file", File(outFile, file))
            }
        }
    }
}
