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
                speed = activityState.activity.speed.value,
                distance = activityState.activity.distance.value,
            ),
            measurementStatus = activityState.measurementStatus,
        )

        fun SerializableActivityState?.toActivityStateOrInitial(): ActivityState {
            return this?.let {
                ActivityState(
                    activity = Activity(
                        speed = Activity.Speed(it.activity.speed),
                        distance = Activity.Distance(it.activity.distance),
                    ),
                    measurementStatus = measurementStatus,
                )
            } ?: ActivityState.INITIAL
        }
    }

    @Serializable
    data class SerializableActivity(
        val speed: Double,
        val distance: Double,
    )
}
