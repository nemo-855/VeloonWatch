package com.nemo.veloon.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PermissionChecker @Inject constructor(@ApplicationContext private val context: Context) {
    fun checkLocationPermission(): String? {
        return if (checkCurrentPermission(Manifest.permission.ACCESS_FINE_LOCATION)) Manifest.permission.ACCESS_FINE_LOCATION
        else if (checkCurrentPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) Manifest.permission.ACCESS_COARSE_LOCATION
        else null
    }

    private fun checkCurrentPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}