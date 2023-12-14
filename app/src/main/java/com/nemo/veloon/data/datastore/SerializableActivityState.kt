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
                averageSpeed = activityState.activity.averageSpeed.value,
                maxSpeed = activityState.activity.maxSpeed.value,
                distance = activityState.activity.distance.value,
                calories = activityState.activity.calories.value,
            ),
            measurementStatus = activityState.measurementStatus,
        )

        fun SerializableActivityState?.toActivityStateOrInitial(): ActivityState {
            return this?.let {
                ActivityState(
                    activity = Activity(
                        averageSpeed = Activity.Speed(it.activity.averageSpeed),
                        maxSpeed = Activity.Speed(it.activity.averageSpeed),
                        distance = Activity.Distance(it.activity.distance),
                        calories = Activity.Calories(it.activity.calories),
                    ),
                    measurementStatus = measurementStatus,
                )
            } ?: ActivityState.INITIAL
        }
    }

    @Serializable
    data class SerializableActivity(
        val averageSpeed: Double,
        val maxSpeed: Double,
        val distance: Double,
        val calories: Double,
    )
}
