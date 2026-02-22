package com.navarro.actions

import com.navarro.core.Logger

object ActionRouter {
    fun routeAction(command: String): String {
        return when {
            command.contains("météo") -> CalendarAction().execute(command)
            command.contains("rappel") -> ReminderAction().execute(command)
            command.contains("message") -> MessageAction().execute(command)
            else -> SystemAction().execute(command)
        }
    }
}
