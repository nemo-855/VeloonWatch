package com.nemo.veloon.ui

import android.Manifest.permission.BODY_SENSORS
import android.Manifest.permission.BODY_SENSORS_BACKGROUND
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.nemo.veloon.data.foregroundservice.BikingActivityService
import com.nemo.veloon.ui.home.HomeRoute
import com.nemo.veloon.ui.theme.VeloonTheme
import com.nemo.veloon.util.LocationUtils
import com.nemo.veloon.util.PermissionChecker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var permissionChecker: PermissionChecker

    @Inject
    lateinit var locationUtils: LocationUtils

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VeloonTheme {
                HomeRoute(
                    viewModel = homeViewModel,
                    startForegroundService = {
                        val service = Intent(this, BikingActivityService::class.java)
                        startForegroundService(service)
                    },
                    stopForegroundService = {
                        val service = Intent(this, BikingActivityService::class.java)
                        service.action = BikingActivityService.REQUEST_TO_STOP_SERVICE
                        startForegroundService(service)
                    },
                    checkLocationPermission = {
                        permissionChecker.checkCurrentLocationPermission() != null
                    },
                    checkBodySensorsPermission = {
                        if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
                            permissionChecker.checkCurrentBodySensorsPermission() != BODY_SENSORS_BACKGROUND
                        } else {
                            permissionChecker.checkCurrentBodySensorsPermission() != BODY_SENSORS
                        }
                    },
                    checkActivityRecognitionPermission = {
                        permissionChecker.checkCurrentActivityRecognitionPermission() != null
                    },
                    checkBackgroundLocationPermission = {
                        permissionChecker.checkCurrentBackgroundLocationPermission() != null
                    },
                    isLocationProviderEnabled = { locationUtils.isLocationProviderEnabled() },
                )
            }
        }
    }
}
