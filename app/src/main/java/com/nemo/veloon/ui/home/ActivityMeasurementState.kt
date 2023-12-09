package com.nemo.veloon.ui.home

sealed class ActivityMeasurementState {
    object InPreparation : ActivityMeasurementState()

    class InProgress(
        val pace: Double,
        val distance: Double,
    ) : ActivityMeasurementState()
}
