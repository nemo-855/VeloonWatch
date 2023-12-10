package com.nemo.veloon.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemo.veloon.data.repository.BikingActivityRepository
import com.nemo.veloon.ui.home.ActivityMeasurementState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    bikingActivityRepository: BikingActivityRepository
) : ViewModel() {
    val state: StateFlow<ActivityMeasurementState> = combine(
        bikingActivityRepository.isBikingFlow,
        bikingActivityRepository.bikingActivityStateFlow,
    ) { isBiking, activityState ->
        if (!isBiking) {
            ActivityMeasurementState.InPreparation
        } else {
            ActivityMeasurementState.InProgress(
                pace = activityState.activity.pace.value,
                distance =  activityState.activity.distance.value,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ActivityMeasurementState.InPreparation
    )
}