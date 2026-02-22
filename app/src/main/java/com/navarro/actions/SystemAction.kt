package com.navarro.actions

import android.content.Context
import com.navarro.core.Logger

class SystemAction(private val contexte: Context) {

    fun executer(commande: String) {
        Logger.i("SystemAction: exécution pour -> $commande")
        // TODO: Intégrer commandes système (mode silencieux, volume, etc.)
    }
}
