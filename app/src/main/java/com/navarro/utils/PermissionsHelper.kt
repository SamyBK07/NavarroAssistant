package com.navarro.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionsHelper {
    fun checkAndRequestPermission(
        activity: Activity,
        permission: String,
        requestCode: Int,
        onGranted: () -> Unit
    ) {
        if (ContextCompat.checkSelfPermission(activity, permission) ==
            PackageManager.PERMISSION_GRANTED) {
            onGranted()
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
        }
    }
}

