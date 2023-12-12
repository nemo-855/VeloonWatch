package com.nemo.veloon.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PermissionChecker @Inject constructor(@ApplicationContext private val context: Context) {
    fun checkCurrentLocationPermission(): String? {
        return if (checkCurrentPermission(Manifest.permission.ACCESS_FINE_LOCATION)) Manifest.permission.ACCESS_FINE_LOCATION
        else if (checkCurrentPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) Manifest.permission.ACCESS_COARSE_LOCATION
        else null
    }

    fun checkCurrentBodySensorsPermission(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && checkCurrentPermission(Manifest.permission.BODY_SENSORS_BACKGROUND)) {
            Manifest.permission.BODY_SENSORS_BACKGROUND
        } else if (checkCurrentPermission(Manifest.permission.BODY_SENSORS)) {
            Manifest.permission.BODY_SENSORS
        } else null
    }

    fun checkCurrentActivityRecognitionPermission(): String? {
        return if (checkCurrentPermission(Manifest.permission.ACTIVITY_RECOGNITION)) Manifest.permission.ACTIVITY_RECOGNITION
        else null
    }

    private fun checkCurrentPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}