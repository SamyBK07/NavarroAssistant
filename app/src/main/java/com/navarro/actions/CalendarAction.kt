package com.navarro.actions

import android.content.Context
import com.navarro.core.Logger

class CalendarAction(private val contexte: Context) {

    fun executer(commande: String) {
        Logger.i("CalendarAction: exécution pour -> $commande")
        // TODO: Intégrer accès agenda Android
    }
}
