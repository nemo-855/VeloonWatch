package com.nemo.veloon.ui.home

sealed class ActivityMeasurementState {
    object InPreparation : ActivityMeasurementState()

    /**
     * 計測中
     *
     * @param calories 消費カロリー(kcal)
     * @param distance 走行距離(m)
     * @param maxSpeed 走行最高速度(km/h)
     */
    class InProgress(
        val calories: Double,
        val distance: Double,
        val maxSpeed: Double,
    ) : ActivityMeasurementState()
}
