/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.nemo.veloon.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.nemo.veloon.data.foregroundservice.BikingActivityService
import com.nemo.veloon.ui.home.HomeRoute
import com.nemo.veloon.ui.theme.VeloonTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
                    checkLocationPermission = { checkLocationPermission() },
                    requestLocationPermission = { fine, coarse, no ->
                        requestLocationPermission(fine, coarse, no)
                    },
                )
            }
        }
    }

    private fun checkLocationPermission(): String? {
        return if (checkCurrentPermission(ACCESS_FINE_LOCATION)) ACCESS_FINE_LOCATION
        else if (checkCurrentPermission(ACCESS_COARSE_LOCATION)) ACCESS_COARSE_LOCATION
        else null
    }

    private fun checkCurrentPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission(
        onAccessFineLocationGranted: () -> Unit,
        onAccessCoarseLocationGranted: () -> Unit,
        onNoPermissionGranted: () -> Unit,
    ) {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(
                    ACCESS_FINE_LOCATION,
                    false
                ) -> onAccessFineLocationGranted()

                permissions.getOrDefault(
                    ACCESS_COARSE_LOCATION,
                    false
                ) -> onAccessCoarseLocationGranted()

                else -> onNoPermissionGranted()
            }
        }

        locationPermissionRequest.launch(
            arrayOf(
                ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
            )
        )
    }
}