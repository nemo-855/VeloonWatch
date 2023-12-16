package com.nemo.veloon.ui.home

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BODY_SENSORS
import android.Manifest.permission.BODY_SENSORS_BACKGROUND
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.nemo.veloon.R
import com.nemo.veloon.ui.HomeViewModel
import com.nemo.veloon.ui.components.atoms.HugeText
import com.nemo.veloon.ui.theme.VeloonTheme

@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    startForegroundService: () -> Unit,
    stopForegroundService: () -> Unit,
    checkLocationPermission: () -> Boolean,
    checkBodySensorsPermission: () -> Boolean,
    checkActivityRecognitionPermission: () -> Boolean,
    checkBackgroundLocationPermission: () -> Boolean,
    isLocationProviderEnabled: () -> Boolean,
) {
    val context = LocalContext.current
    val state = viewModel.state.collectAsState().value

    val locationPermissionRequest =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                !isLocationProviderEnabled() -> {
                    Toast.makeText(
                        context,
                        R.string.home_panel_location_provider_disabled,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                !checkBackgroundLocationPermission() -> {
                    Toast.makeText(
                        context,
                        R.string.home_panel_location_grand_background_location_permissions,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                !checkAllNecessaryPermissionsAreGranted(permissions) -> {
                    Toast.makeText(
                        context,
                        R.string.home_panel_location_grand_all_permissions,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    startForegroundService()
                }
            }
        }


    Scaffold {
        HomePanel(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onStartButtonClicked = {
                // TODO ACCESS_FINE_LOCATIONでないと正しく取れないよということを伝える
                if (checkLocationPermission() && checkBodySensorsPermission() && checkActivityRecognitionPermission()) {
                    startForegroundService()
                } else {
                    locationPermissionRequest.launch(getNecessaryPermissions())
                }
            },
            onFinishButtonClicked = {
                stopForegroundService()
            },
        )
    }
}

@Composable
private fun HomePanel(
    modifier: Modifier = Modifier,
    state: ActivityMeasurementState,
    onStartButtonClicked: () -> Unit,
    onFinishButtonClicked: () -> Unit,
) {
    when (state) {
        is ActivityMeasurementState.InPreparation -> {
            Content(
                modifier = modifier,
                buttonPainter = painterResource(id = R.drawable.play_outlined),
                onClick = onStartButtonClicked,
                textContent = {
                    inPreparationText()
                }
            )
        }

        is ActivityMeasurementState.InProgress -> {
            Content(
                modifier = modifier,
                buttonPainter = painterResource(id = R.drawable.stop_outlined),
                onClick = onFinishButtonClicked,
                textContent = {
                    inProgressText(
                        calories = state.calories.toString(),
                        distance = state.distance.toString(),
                        maxSpeed = state.maxSpeed.toString(),
                    )
                }
            )
        }
    }
}

private fun ScalingLazyListScope.inPreparationText() {
    item {
        HugeText(
            text = stringResource(id = R.string.home_panel_click_start),
            color = MaterialTheme.colors.primary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
        )
    }
}

private fun ScalingLazyListScope.inProgressText(
    calories: String,
    distance: String,
    maxSpeed: String,
) {
    item {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = stringResource(id = R.string.home_panel_calories),
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.body1,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                HugeText(
                    modifier = Modifier.weight(1f),
                    text = calories,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(id = R.string.home_panel_calories_unit),
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1,
                )
            }
        }
    }

    item {
        Spacer(modifier = Modifier.size(4.dp))
    }

    item {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = stringResource(id = R.string.home_panel_distance),
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.body1,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                HugeText(
                    modifier = Modifier.weight(1f),
                    text = distance,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(id = R.string.home_panel_distance_unit),
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1,
                )
            }
        }
    }

    item {
        Spacer(modifier = Modifier.size(4.dp))
    }

    item {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = stringResource(id = R.string.home_panel_speed),
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.body1,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                HugeText(
                    modifier = Modifier.weight(1f),
                    text = maxSpeed,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(id = R.string.home_panel_speed_unit),
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1,
                )
            }
        }
    }
}

/**
 * 必要なPermissionsを全て取得する
 */

private fun getNecessaryPermissions(): Array<String> {
    val locationPermissions = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
    val bodySensorsPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(BODY_SENSORS, BODY_SENSORS_BACKGROUND)
    } else {
        arrayOf(BODY_SENSORS)
    }
    val activityRecognitionPermissions = arrayOf(Manifest.permission.ACTIVITY_RECOGNITION)

    return locationPermissions + bodySensorsPermissions + activityRecognitionPermissions
}

private fun checkAllNecessaryPermissionsAreGranted(permissions:  Map<String, @JvmSuppressWildcards Boolean>): Boolean {
    val isLocationGranted = permissions.getOrDefault(ACCESS_FINE_LOCATION, false)
    val isBodySensorsGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.getOrDefault(BODY_SENSORS_BACKGROUND, false)
    } else {
        permissions.getOrDefault(BODY_SENSORS, false)
    }
    val isActivityRecognitionGranted = permissions.getOrDefault(Manifest.permission.ACTIVITY_RECOGNITION, false)

    return isLocationGranted && isBodySensorsGranted && isActivityRecognitionGranted
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    buttonPainter: Painter,
    onClick: () -> Unit,
    textContent: ScalingLazyListScope.() -> Unit,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            textContent()
        }
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colors.background)
                .padding(4.dp)
                .clickable(onClick = onClick)
                .padding(4.dp)
                .size(36.dp),
            painter = buttonPainter,
            tint = MaterialTheme.colors.secondary,
            contentDescription = stringResource(id = R.string.home_panel_start_button),
        )
    }
}

private class PreviewProvider : PreviewParameterProvider<ActivityMeasurementState> {
    override val values: Sequence<ActivityMeasurementState>
        get() = sequenceOf(
            ActivityMeasurementState.InPreparation,
            ActivityMeasurementState.InProgress(
                calories = 0.0,
                distance = 0.0,
                maxSpeed = 0.0,
            ),
            ActivityMeasurementState.InProgress(
                calories = 4000.0,
                distance = 200.0,
                maxSpeed = 40.0,
            ),
        )
}

@Preview(
    device = Devices.WEAR_OS_SMALL_ROUND,
    showBackground = true,
)
@Composable
private fun HomePanelPreview(
    @PreviewParameter(PreviewProvider::class) homeState: ActivityMeasurementState,
) {
    VeloonTheme {
        Scaffold(modifier = Modifier.background(MaterialTheme.colors.background)) {
            HomePanel(
                state = homeState,
                onStartButtonClicked = {},
                onFinishButtonClicked = {},
            )
        }
    }
}