package com.nemo.veloon.ui

import android.content.Intent
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
                    checkLocationPermission = { permissionChecker.checkLocationPermission() },
                    isLocationProviderEnabled = { locationUtils.isLocationProviderEnabled() },
                )
            }
        }
    }
}
