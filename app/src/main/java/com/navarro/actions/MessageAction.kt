package com.navarro.actions

import android.content.Context
import com.navarro.core.Logger

class MessageAction(private val contexte: Context) {

    fun executer(commande: String) {
        Logger.i("MessageAction: exécution pour -> $commande")
        // TODO: Intégrer envoi SMS ou WhatsApp
    }
}
