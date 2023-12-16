package com.nemo.veloon.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemo.veloon.data.repository.BikingActivityRepository
import com.nemo.veloon.ui.home.ActivityMeasurementState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
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
                calories = activityState.activity.calories.value.formattedContent("%.1f"),
                distance =  activityState.activity.distance.value.formattedContent("%.1f"),
                maxSpeed = activityState.activity.maxSpeed.value.formattedContent("%.1f"),
            )
        }
    }.catch {throwable ->
        if (throwable is CancellationException) {
            // no-op
        } else {
            // TODO 適切にUIに反映する
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ActivityMeasurementState.InPreparation
    )
}