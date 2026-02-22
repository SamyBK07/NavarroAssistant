package com.navarro.core

object AppConfig {

    // ---------- APP ----------
    const val APP_NAME = "Navarro Assistant"
    const val TAG = "Navarro"

    // ---------- VOSK ----------
    const val MODEL_NAME = "vosk-model-small-fr-0.22"
    const val MODEL_ZIP_NAME = "vosk-model-small-fr-0.22.zip"
    const val MODEL_DOWNLOAD_URL =
        "https://alphacephei.com/vosk/models/vosk-model-small-fr-0.22.zip"

    // ---------- NOTIFICATION ----------
    const val NOTIFICATION_CHANNEL_ID = "navarro_hotword_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Assistant actif"
    const val NOTIFICATION_ID = 1001

    // ---------- ACTIONS ----------
    const val ACTION_START_HOTWORD = "com.navarro.action.START_HOTWORD"
    const val ACTION_STOP_HOTWORD = "com.navarro.action.STOP_HOTWORD"
    const val ACTION_START_COMMAND = "com.navarro.action.START_COMMAND"
    const val ACTION_STOP_COMMAND = "com.navarro.action.STOP_COMMAND"

    // ---------- FILES ----------
    const val MODELS_DIR = "models"

}
