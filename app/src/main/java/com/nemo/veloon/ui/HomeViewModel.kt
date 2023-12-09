package com.nemo.veloon.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemo.veloon.data.sensor.ActivitySensor
import com.nemo.veloon.ui.home.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val activitySensor: ActivitySensor
): ViewModel() {
    private val isStarted = MutableStateFlow(false)

    val state: StateFlow<HomeState> = combine(isStarted, activitySensor.current) {
        isStarted, activityState ->
        if (!isStarted) {
            HomeState.InPreparation
        } else {
            HomeState.InProgress(
                pace = activityState.activity.pace.value
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HomeState.InPreparation
    )

    fun onStartButtonClicked() {
        viewModelScope.launch {
            isStarted.update { true }
            activitySensor.start()
        }
    }

    fun onFinishButtonClicked() {
        viewModelScope.launch {
            isStarted.update { false }
            activitySensor.stop()
        }
    }
}