package com.navarro.actions

import java.text.Normalizer

object ActionRouter {

    private fun normalize(text: String): String {
        return Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }

    fun routeAction(command: String): String {
        val cleanCommand = normalize(command)

        return when {
            cleanCommand.contains("meteo") -> WeatherAction().execute(command)
            cleanCommand.contains("rappel") -> ReminderAction().execute(command)
            cleanCommand.contains("message") -> MessageAction().execute(command)
            else -> SystemAction().execute(command)
        }
    }
}
