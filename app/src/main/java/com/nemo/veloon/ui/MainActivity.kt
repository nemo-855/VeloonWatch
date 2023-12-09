/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.nemo.veloon.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.nemo.veloon.data.foregroundservice.ActivityService
import com.nemo.veloon.ui.home.HomeRoute
import com.nemo.veloon.ui.theme.VeloonTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VeloonTheme {
                HomeRoute(
                    mainActivityViewModel = viewModel,
                    startForegroundService = {
                        val service = Intent(this, ActivityService::class.java)
                        startForegroundService(service)
                    },
                    stopForegroundService = {
                        val service = Intent(this, ActivityService::class.java)
                        stopService(service)
                    }
                )
            }
        }
    }
}
