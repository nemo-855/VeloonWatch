package com.nemo.veloon.domain

/**
 * 計測情報
 *
 * @param activity 走行情報
 * @param measurementStatus 情報の計測状態
 */
data class ActivityState(
    val activity: Activity,
    val measurementStatus: MeasurementStatus,
) {
    companion object {
        val INITIAL = ActivityState(
            activity = Activity.EMPTY,
            measurementStatus = MeasurementStatus.LOADING,
        )
    }

    fun copyActivity(
        averageSpeed: Activity.Speed? = null,
        maxSpeed: Activity.Speed? = null,
        distance: Activity.Distance? = null,
    ): ActivityState {
        return copy(
            activity = activity.copy(
                averageSpeed = averageSpeed ?: activity.averageSpeed,
                maxSpeed = maxSpeed ?: activity.maxSpeed,
                distance = distance ?: activity.distance,
            )
        )
    }

    enum class MeasurementStatus {
        LOADING,
        PERMISSION_DENIED,
        ERROR,
        SUCCESS,
        FINISHED,
    }
}
