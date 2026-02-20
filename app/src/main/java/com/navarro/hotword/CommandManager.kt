// CommandManager.kt
package com.navarro

object CommandManager {
    private val localCommands = mapOf(
        "heure" to { /* Logique pour afficher l'heure */ },
        "ouvre youtube" to { /* Logique pour ouvrir YouTube */ }
        // Ajoute d'autres commandes ici
    )

    fun executeCommand(command: String, callback: (String) -> Unit) {
        localCommands[command]?.let {
            callback("Commande exécutée : $command")
        } ?: run {
            // Envoie à l'IA externe si la commande n'est pas locale
            SmartApi.send(command, callback)
        }
    }
}
