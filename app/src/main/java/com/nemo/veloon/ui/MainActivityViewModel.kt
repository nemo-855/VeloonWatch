package com.nemo.veloon.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemo.veloon.data.sensor.ActivitySensor
import com.nemo.veloon.ui.home.ActivityMeasurementState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    activitySensor: ActivitySensor,
): ViewModel() {
    private val isStartedMeasurement = MutableStateFlow(false)

    val state: StateFlow<ActivityMeasurementState> = combine(isStartedMeasurement, activitySensor.current) {
        isStarted, activityState ->
        if (!isStarted) {
            ActivityMeasurementState.InPreparation
        } else {
            // TODO: ここでActivityStateに応じてエラーハンドリングをする
            ActivityMeasurementState.InProgress(
                pace = activityState.activity.pace.value
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ActivityMeasurementState.InPreparation
    )

    fun onStartButtonClicked() {
        isStartedMeasurement.update { true }
    }

    fun onFinishButtonClicked() {
        isStartedMeasurement.update { false }
    }
}