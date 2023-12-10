package com.nemo.veloon.ui.home

sealed class ActivityMeasurementState {
    object InPreparation : ActivityMeasurementState()

    class InProgress(
        val speed: Double,
        val distance: Double,
    ) : ActivityMeasurementState()
}
