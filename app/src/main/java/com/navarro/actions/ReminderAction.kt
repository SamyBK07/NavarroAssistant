package com.navarro.actions

import android.content.Context
import com.navarro.core.Logger

class ReminderAction(private val contexte: Context) {

    fun executer(commande: String) {
        Logger.i("ReminderAction: exécution pour -> $commande")
        // TODO: Intégrer création de rappel ou alarme
    }
}
