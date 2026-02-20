package com.navarro

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object CommandManager {

    // Contexte nécessaire pour certaines actions (à passer depuis MainActivity ou HotwordService)
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val localCommands = mapOf(
        // ========== HEURE ET DATE ==========
        "heure" to { "Il est ${LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))}." },
        "date" to { "Nous sommes le ${java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}." },

        // ========== APPLICATIONS ==========
        "ouvre youtube" to {
            openApp("com.google.android.youtube", "YouTube")
        },
        "ouvre chrome" to {
            openApp("com.android.chrome", "Chrome")
        },
        "ouvre paramètres" to {
            appContext.startActivity(Intent(Settings.ACTION_SETTINGS))
            "J'ouvre les paramètres."
        },
        "ouvre appareil photo" to {
            appContext.startActivity(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
            "J'ouvre l'appareil photo."
        },

        // ========== MULTIMÉDIA ==========
        "augmente le volume" to {
            adjustVolume(AudioManager.ADJUST_RAISE)
        },
        "baisse le volume" to {
            adjustVolume(AudioManager.ADJUST_LOWER)
        },
        "coupe le son" to {
            adjustVolume(AudioManager.ADJUST_MUTE)
        },
        "remets le son" to {
            adjustVolume(AudioManager.ADJUST_UNMUTE)
        },
        "lecture" to {
            simulateMediaButton(Intent.ACTION_MEDIA_PLAY)
        },
        "pause" to {
            simulateMediaButton(Intent.ACTION_MEDIA_PAUSE)
        },
        "suivant" to {
            simulateMediaButton(Intent.ACTION_MEDIA_NEXT)
        },
        "précédent" to {
            simulateMediaButton(Intent.ACTION_MEDIA_PREVIOUS)
        },

        // ========== ALARMES ET MINUTEURS ==========
        "ajoute une alarme" to {
            appContext.startActivity(Intent(AlarmClock.ACTION_SET_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
            "Ouvre l'application d'alarme pour ajouter une nouvelle alarme."
        },
        "ajoute un minuteur" to {
            appContext.startActivity(Intent(AlarmClock.ACTION_SET_TIMER).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
            "Ouvre l'application d'horloge pour ajouter un minuteur."
        },

        // ========== TÉLÉPHONE ==========
        "appelle [contact]" to { text: String ->
            val name = text.replace("appelle ", "").trim()
            callContact(name)
        },
        "envoie un message à [contact]" to { text: String ->
            val name = text.replace("envoie un message à ", "").trim()
            sendSMS(name)
        },

        // ========== RÉSEAUX ==========
        "active le wifi" to {
            toggleWifi(true)
        },
        "désactive le wifi" to {
            toggleWifi(false)
        },
        "active le bluetooth" to {
            toggleBluetooth(true)
        },
        "désactive le bluetooth" to {
            toggleBluetooth(false)
        },

        // ========== NAVIGATION ==========
        "ouvre maps" to {
            openApp("com.google.android.apps.maps", "Google Maps")
        },
        "comment rentrer à la maison" to {
            appContext.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=maison")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
            "Je t'indique le chemin pour rentrer."
        },

        // ========== DIVERS ==========
        "active la lampe torche" to {
            toggleFlashlight(true)
        },
        "désactive la lampe torche" to {
            toggleFlashlight(false)
        },
        "quand est le prochain coucher de soleil" to {
            getNextSunset()
        },
        "prend une note" to {
            appContext.startActivity(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
            "Ouvre une nouvelle note."
        },
        "recharge" to {
            "Je ne peux pas me recharger tout seul, branche-moi à une prise !"
        },
        "qui es-tu" to {
            "Je suis Navarro, ton assistant vocal personnel. Je suis là pour t'aider !"
        }
    )

    // ========== FONCTIONS AUXILIAIRES ==========
    private fun openApp(packageName: String, appName: String): String {
        return try {
            val intent = appContext.packageManager.getLaunchIntentForPackage(packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (intent != null) {
                appContext.startActivity(intent)
                "J'ouvre $appName."
            } else {
                "Désolé, $appName n'est pas installé."
            }
        } catch (e: Exception) {
            "Erreur lors de l'ouverture de $appName."
        }
    }

    private fun adjustVolume(adjustType: Int): String {
        val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, adjustType, 0)
        return when (adjustType) {
            AudioManager.ADJUST_RAISE -> "Volume augmenté."
            AudioManager.ADJUST_LOWER -> "Volume baissé."
            AudioManager.ADJUST_MUTE -> "Son coupé."
            AudioManager.ADJUST_UNMUTE -> "Son rétabli."
            else -> "Volume ajusté."
        }
    }

    private fun simulateMediaButton(action: String): String {
        appContext.sendBroadcast(Intent(action).apply {
            flags = Intent.FLAG_RECEIVER_FOREGROUND
        })
        return "Commande multimédia envoyée."
    }

    private fun callContact(name: String): String {
        // Note: Pour appeler un contact, il faudrait accéder à la liste des contacts (permission REQUIRED)
        return "Pour appeler $name, je dois accéder à tes contacts. Autorise-moi d'abord dans les paramètres."
    }

    private fun sendSMS(name: String): String {
        // Note: Pour envoyer un SMS, il faudrait accéder à la liste des contacts (permission REQUIRED)
        return "Pour envoyer un message à $name, je dois accéder à tes contacts. Autorise-moi d'abord dans les paramètres."
    }

    private fun toggleWifi(enable: Boolean): String {
        // Note: Changement direct du WiFi nécessite des permissions spéciales ou une activité utilisateur
        appContext.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
        return if (enable) "Ouvre les paramètres WiFi pour l'activer."
               else "Ouvre les paramètres WiFi pour le désactiver."
    }

    private fun toggleBluetooth(enable: Boolean): String {
        // Note: Changement direct du Bluetooth nécessite des permissions spéciales ou une activité utilisateur
        appContext.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
        return if (enable) "Ouvre les paramètres Bluetooth pour l'activer."
               else "Ouvre les paramètres Bluetooth pour le désactiver."
    }

    private fun toggleFlashlight(enable: Boolean): String {
        // Note: Allumer la lampe torche nécessite une implémentation spécifique selon l'appareil
        return if (enable) "La lampe torche n'est pas disponible directement. Utilise une application dédiée."
               else "Lampe torche désactivée."
    }

    private fun getNextSunset(): String {
        // Note: Nécessite un appel API ou une librairie pour obtenir l'heure du coucher de soleil
        return "Je ne peux pas obtenir cette information pour l'instant."
    }

    // ========== LOGIQUE PRINCIPALE ==========
    fun executeCommand(command: String, callback: (String) -> Unit) {
        // Vérifie d'abord les commandes avec des paramètres dynamiques
        localCommands.entries.firstOrNull { (key, _) ->
            command.matches(Regex(key.replace("[contact]", ".*")))
        }?.let { (key, action) ->
            val response = if (action is Function1<*, *>) {
                (action as (String) -> String)(command)
            } else {
                action.toString()
            }
            callback(response)
            return
        }

        // Vérifie les commandes simples
        localCommands[command]?.let { action ->
            val response = if (action is Function0<*>) {
                (action as () -> String)()
            } else {
                action.toString()
            }
            callback(response)
            return
        }

        // Si la commande n'est pas locale, envoie à Mistral
        ApiClient.sendToMistral(command, callback)
    }
}
