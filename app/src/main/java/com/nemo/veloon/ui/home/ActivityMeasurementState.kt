package com.nemo.veloon.ui.home

sealed class ActivityMeasurementState {
    object InPreparation : ActivityMeasurementState()

    /**
     * 計測中
     *
     * @param maxSpeed 最高の走行速度(km/h)
     * @param distance 走行距離(m)
     */
    class InProgress(
        val maxSpeed: Double,
        val distance: Double,
    ) : ActivityMeasurementState()
}
