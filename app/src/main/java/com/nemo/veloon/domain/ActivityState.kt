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
            measurementStatus = MeasurementStatus.LOCATION_ACQUIRING,
        )
    }

    fun copyActivity(
        pace: Activity.Pace? = null,
        distance: Activity.Distance? = null,
    ): ActivityState {
        return copy(
            activity = activity.copy(
                pace = pace ?: activity.pace,
                distance = distance ?: activity.distance,
            )
        )
    }

    enum class MeasurementStatus {
        LOCATION_ACQUIRED,
        LOCATION_ACQUIRING,
        LOCATION_PERMISSTION_DENIED,
        LOCATION_ERROR,
    }
}
