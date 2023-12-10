package com.nemo.veloon.data.datastore

import com.nemo.veloon.domain.Activity
import com.nemo.veloon.domain.ActivityState
import kotlinx.serialization.Serializable

@Serializable
data class SerializableActivityState(
    val activity: SerializableActivity,
    val measurementStatus: ActivityState.MeasurementStatus,
) {
    companion object {
        fun from(activityState: ActivityState) = SerializableActivityState(
            activity = SerializableActivity(
                pace = activityState.activity.pace.value,
                distance = activityState.activity.distance.value,
            ),
            measurementStatus = activityState.measurementStatus,
        )

        fun SerializableActivityState?.toActivityStateOrInitial(): ActivityState {
            return this?.let {
                ActivityState(
                    activity = Activity(
                        pace = Activity.Pace(it.activity.pace),
                        distance = Activity.Distance(it.activity.distance),
                    ),
                    measurementStatus = measurementStatus,
                )
            } ?: ActivityState.INITIAL
        }
    }

    @Serializable
    data class SerializableActivity(
        val pace: Double,
        val distance: Double,
    )
}
