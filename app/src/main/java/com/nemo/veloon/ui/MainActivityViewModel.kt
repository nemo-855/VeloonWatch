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
class MainActivityViewModel @Inject constructor(
    bikingActivityRepository: BikingActivityRepository
) : ViewModel() {
    val state: StateFlow<ActivityMeasurementState> = combine(
        bikingActivityRepository.isBikingFlow,
        bikingActivityRepository.bikingPaceFlow,
        bikingActivityRepository.bikingDistanceFlow,
    ) { isBiking, pace, distance ->
        if (!isBiking) {
            ActivityMeasurementState.InPreparation
        } else {
            ActivityMeasurementState.InProgress(
                pace = pace.value,
                distance = distance.value,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ActivityMeasurementState.InPreparation
    )
}