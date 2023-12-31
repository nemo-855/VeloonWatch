package com.nemo.veloon.data.sensor

import android.content.Context
import androidx.health.services.client.ExerciseClient
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.HealthServices
import androidx.health.services.client.awaitWithException
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseTrackedStatus.Companion.NO_EXERCISE_IN_PROGRESS
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.LocationAvailability
import androidx.health.services.client.endExercise
import com.nemo.veloon.domain.Activity
import com.nemo.veloon.domain.ActivityState
import com.nemo.veloon.domain.InAppException
import com.nemo.veloon.util.SafeDouble.Companion.toSafe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ActivitySensorImpl(context: Context) :
    ActivitySensor {
    private val exerciseClient = HealthServices.getClient(context).exerciseClient

    private val _current = MutableStateFlow(ActivityState.INITIAL)
    override val current = _current

    private val necessaryDataTypes = setOf(
        DataType.SPEED_STATS,
        DataType.CALORIES_TOTAL,
        DataType.PACE_STATS,
        DataType.DISTANCE_TOTAL,
    )

    override suspend fun start() {
        // デバイス全体において既存のExerciseがあるかどうかを確認する
        if (!existsNoOtherExerciseInProgress(exerciseClient)) {
            throw InAppException.ActivityMeasurementException.AnotherExerciseIsInProgress
        }

        // 自転車の計測がサポートされているかどうかを確認する
        if (!isBikingSupported(exerciseClient)) {
            throw InAppException.ActivityMeasurementException.BikingMeasurementIsNotSupported
        }

        // 必要なデータ型がサポートされているかどうかを確認する
        if (!isAllNecessaryDataTypesAvailable(exerciseClient, necessaryDataTypes)) {
            throw InAppException.ActivityMeasurementException.BikingMeasurementIsNotSupported
        }

        _current.update { it.copy(activity = Activity.EMPTY) }
        exerciseClient.setExerciseUpdateCallback()
        exerciseClient.startExercise(necessaryDataTypes)
    }

    override suspend fun stop() {
        exerciseClient.endExercise()
    }


    private suspend fun isBikingSupported(exerciseClient: ExerciseClient): Boolean {
        val capabilities = exerciseClient.getCapabilitiesAsync().awaitWithException()
        return ExerciseType.BIKING in capabilities.supportedExerciseTypes
    }

    private suspend fun isAllNecessaryDataTypesAvailable(
        exerciseClient: ExerciseClient,
        necessaryDataTypes: Set<DataType<*, *>>
    ): Boolean {
        val capabilities = exerciseClient.getCapabilitiesAsync().awaitWithException().getExerciseTypeCapabilities(ExerciseType.BIKING)
        return necessaryDataTypes.all { it in capabilities.supportedDataTypes }
    }

    private suspend fun existsNoOtherExerciseInProgress(exerciseClient: ExerciseClient): Boolean {
        val exerciseInfo = exerciseClient.getCurrentExerciseInfoAsync().awaitWithException()
        return exerciseInfo.exerciseTrackedStatus == NO_EXERCISE_IN_PROGRESS
    }

    private fun ExerciseClient.setExerciseUpdateCallback() {
        val callback = object : ExerciseUpdateCallback {
            override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
                if (update.exerciseStateInfo.state.isEnded) {
                    _current.update { it.copy(measurementStatus = ActivityState.MeasurementStatus.FINISHED) }
                } else {
                    // ExerciseUpdate doc ↓
                    // https://developer.android.com/reference/androidx/health/services/client/data/ExerciseUpdate
                    val latestMetrics = update.latestMetrics
                    latestMetrics.getData(DataType.PACE_STATS)?.let { stats ->
                        val msPerKmToKmPerH: (Double) -> Double = { 1 * 3600 * 1000 / it }
                        _current.update { it.copyActivity(
                            averageSpeed  = Activity.Speed(msPerKmToKmPerH(stats.average).toSafe()),
                            maxSpeed = Activity.Speed((msPerKmToKmPerH(stats.max)).toSafe()),
                        ) }
                    }
                    latestMetrics.getData(DataType.DISTANCE_TOTAL)?.total?.let { distance ->
                        val mToKm: (Double) -> Double = { it / 1000 }
                        _current.update { it.copyActivity(distance = Activity.Distance(mToKm(distance).toSafe())) }
                    }
                    latestMetrics.getData(DataType.CALORIES_TOTAL)?.total?.let { calories ->
                        _current.update { it.copyActivity(calories = Activity.Calories(calories.toSafe())) }
                    }
                }
            }

            override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) {
                // no-op
            }

            override fun onRegistered() {
                // no-op
            }

            override fun onRegistrationFailed(throwable: Throwable) {
                _current.update {
                    it.copy(measurementStatus = ActivityState.MeasurementStatus.ERROR)
                }
            }

            override fun onAvailabilityChanged(
                dataType: DataType<*, *>,
                availability: Availability
            ) {
                when (availability) {
                    LocationAvailability.UNKNOWN,
                    LocationAvailability.UNAVAILABLE -> {
                        _current.update {
                            it.copy(measurementStatus = ActivityState.MeasurementStatus.ERROR)
                        }
                    }

                    LocationAvailability.NO_GNSS -> {
                        _current.update {
                            it.copy(measurementStatus = ActivityState.MeasurementStatus.PERMISSION_DENIED)
                        }
                    }

                    LocationAvailability.ACQUIRING -> {
                        _current.update {
                            it.copy(measurementStatus = ActivityState.MeasurementStatus.LOADING)
                        }
                    }
                    LocationAvailability.ACQUIRED_TETHERED,
                    LocationAvailability.ACQUIRED_UNTETHERED -> {
                        _current.update {
                            it.copy(measurementStatus = ActivityState.MeasurementStatus.SUCCESS)
                        }
                    }
                }
            }
        }
        this.setUpdateCallback(callback)
    }

    private suspend fun ExerciseClient.startExercise(
        dataTypes: Set<DataType<*, *>>,
    ) {
        val config = ExerciseConfig(
            exerciseType = ExerciseType.BIKING,
            dataTypes = dataTypes,
            isAutoPauseAndResumeEnabled = false,
            isGpsEnabled = true,
            exerciseGoals = emptyList(),
        )
        this.startExerciseAsync(config).awaitWithException()
    }
}