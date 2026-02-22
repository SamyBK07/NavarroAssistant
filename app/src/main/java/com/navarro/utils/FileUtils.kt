package com.navarro.utils

import java.io.File

object FileUtils {
    fun ensureDirectoryExists(dir: File): Boolean {
        return dir.exists() || dir.mkdirs()
    }
}
