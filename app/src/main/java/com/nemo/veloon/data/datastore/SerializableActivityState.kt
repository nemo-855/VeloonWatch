package com.nemo.veloon.data.datastore

import com.nemo.veloon.domain.Activity
import com.nemo.veloon.domain.ActivityState
import com.nemo.veloon.util.SafeDouble.Companion.toSafe
import kotlinx.serialization.Serializable

@Serializable
data class SerializableActivityState(
    val activity: SerializableActivity,
    val measurementStatus: ActivityState.MeasurementStatus,
) {
    companion object {
        fun from(activityState: ActivityState) = SerializableActivityState(
            activity = SerializableActivity(
                averageSpeed = activityState.activity.averageSpeed.value.getValue(),
                maxSpeed = activityState.activity.maxSpeed.value.getValue(),
                distance = activityState.activity.distance.value.getValue(),
                calories = activityState.activity.calories.value.getValue(),
            ),
            measurementStatus = activityState.measurementStatus,
        )

        fun SerializableActivityState?.toActivityStateOrInitial(): ActivityState {
            return this?.let {
                ActivityState(
                    activity = Activity(
                        averageSpeed = Activity.Speed(it.activity.averageSpeed.toSafe()),
                        maxSpeed = Activity.Speed(it.activity.averageSpeed.toSafe()),
                        distance = Activity.Distance(it.activity.distance.toSafe()),
                        calories = Activity.Calories(it.activity.calories.toSafe()),
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
