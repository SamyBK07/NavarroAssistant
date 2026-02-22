package com.navarro.ai

interface AIManager {
    suspend fun processCommand(command: String): String
}
