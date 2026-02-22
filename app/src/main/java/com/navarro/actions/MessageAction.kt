package com.navarro.actions

class MessageAction : BaseAction() {
    override fun execute(command: String): String {
        // TODO: Envoi de SMS/email
        return "Action message pour: $command (non implémentée)"
    }
}
