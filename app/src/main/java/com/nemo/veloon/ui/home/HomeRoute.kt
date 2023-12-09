package com.nemo.veloon.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.nemo.veloon.R
import com.nemo.veloon.ui.MainActivityViewModel
import com.nemo.veloon.ui.components.atoms.HugeText
import com.nemo.veloon.ui.theme.VeloonTheme

@Composable
fun HomeRoute(
    mainActivityViewModel: MainActivityViewModel,
    startForegroundService: () -> Unit,
    stopForegroundService: () -> Unit,
) {
    val state = mainActivityViewModel.state.collectAsState().value

    Scaffold {
        HomePanel(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onStartButtonClicked = {
                mainActivityViewModel.onStartButtonClicked()
                startForegroundService()
            },
            onFinishButtonClicked = {
                mainActivityViewModel.onFinishButtonClicked()
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
                text = {
                    InPreparationText()
                }
            )
        }

        is ActivityMeasurementState.InProgress -> {
            Content(
                modifier = modifier,
                buttonPainter = painterResource(id = R.drawable.stop_outlined),
                onClick = onFinishButtonClicked,
                text = {
                    InProgressText(
                        pace = state.pace.toString(),
                    )
                }
            )
        }
    }
}

@Composable
private fun InPreparationText(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(id = R.string.home_panel_click_start),
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.body1,
    )
}

@Composable
private fun InProgressText(
    modifier: Modifier = Modifier,
    pace: String,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = stringResource(id = R.string.home_panel_pace),
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.body1,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                HugeText(
                    modifier = Modifier.weight(1f),
                    text = pace,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(id = R.string.home_panel_pace_unit),
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1,
                )
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

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
                    text = "50.0",
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
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    buttonPainter: Painter,
    onClick: () -> Unit,
    text: @Composable BoxScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(
                    top = 24.dp,
                    start = 48.dp,
                    end = 48.dp,
                ),
            contentAlignment = Alignment.Center,
        ) {
            text()
        }
        Icon(
            modifier = Modifier
                .padding(8.dp)
                .clickable(onClick = onClick)
                .padding(8.dp)
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
                pace = 0.0,
            ),
        )
}

@Preview(
    device = Devices.WEAR_OS_LARGE_ROUND,
    showBackground = true,
)
@Composable
private fun HomePanelPreview(
    @PreviewParameter(PreviewProvider::class) homeState: ActivityMeasurementState,
) {
    VeloonTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
        ) {
            HomePanel(
                state = homeState,
                onStartButtonClicked = {},
                onFinishButtonClicked = {},
            )
        }
    }
}