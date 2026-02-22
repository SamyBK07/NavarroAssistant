package com.navarro.actions

import android.content.Context
import com.navarro.core.Logger

/**
 * Route les actions détectées par l'IA vers le module correspondant
 */
class ActionRouter(private val contexte: Context) {

    private val calendarAction = CalendarAction(contexte)
    private val messageAction = MessageAction(contexte)
    private val reminderAction = ReminderAction(contexte)
    private val systemAction = SystemAction(contexte)

    /**
     * Exécute l'action correspondante selon la commande
     */
    fun executerAction(commande: String) {
        Logger.i("ActionRouter: commande reçue -> $commande")

        when {
            commande.contains("rendez-vous", true) ||
            commande.contains("agenda", true) -> calendarAction.executer(commande)

            commande.contains("message", true) ||
            commande.contains("sms", true) -> messageAction.executer(commande)

            commande.contains("rappel", true) ||
            commande.contains("alarme", true) -> reminderAction.executer(commande)

            commande.contains("système", true) ||
            commande.contains("paramètre", true) -> systemAction.executer(commande)

            else -> Logger.i("ActionRouter: aucune action correspondante, réponse vocale uniquement")
        }
    }
}
