package com.nemo.veloon.data.sensor

import android.content.Context
import androidx.health.services.client.ExerciseClient
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.HealthServices
import androidx.health.services.client.awaitWithException
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseTrackedStatus.Companion.NO_EXERCISE_IN_PROGRESS
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.LocationAvailability
import com.nemo.veloon.domain.Activity
import com.nemo.veloon.domain.ActivityState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ActivitySensorImpl(context: Context) :
    ActivitySensor {
    private val exerciseClient = HealthServices.getClient(context).exerciseClient

    private val _current = MutableStateFlow(ActivityState.INITIAL)
    override val current = _current.asStateFlow()

    override suspend fun start() {
        // デバイス全体において既存のExerciseがあるかどうかを確認する
        if (!existsNoOtherExerciseInProgress(exerciseClient)) {
            throw ActivitySensor.ActivitySensorException.AnotherExerciseIsInProgress
        }

        // 自転車の計測がサポートされているかどうかを確認する
        if (!isBikingSupported(exerciseClient)) {
            throw ActivitySensor.ActivitySensorException.BikingMeasurementIsNotSupported
        }

        // 必要なデータ型がサポートされているかどうかを確認する
        if (!isAllNecessaryDataTypesAvailable(exerciseClient)) {
            throw ActivitySensor.ActivitySensorException.BikingMeasurementIsNotSupported
        }

        _current.update { it.copy(activity = Activity.EMPTY) }
        exerciseClient.setExerciseUpdateCallback()
    }

    override suspend fun stop() {
        // no-op
    }


    private suspend fun isBikingSupported(exerciseClient: ExerciseClient): Boolean {
        val capabilities = exerciseClient.getCapabilitiesAsync().awaitWithException()
        return ExerciseType.BIKING in capabilities.supportedExerciseTypes
    }

    private suspend fun isAllNecessaryDataTypesAvailable(exerciseClient: ExerciseClient): Boolean {
        val capabilities = exerciseClient.getCapabilitiesAsync().awaitWithException().getExerciseTypeCapabilities(ExerciseType.BIKING)
        val necessaryDataTypes = setOf(
            DataType.SPEED,
            DataType.DISTANCE,
        )
        return necessaryDataTypes.all { it in capabilities.supportedDataTypes }
    }

    private suspend fun existsNoOtherExerciseInProgress(exerciseClient: ExerciseClient): Boolean {
        val exerciseInfo = exerciseClient.getCurrentExerciseInfoAsync().awaitWithException()
        return exerciseInfo.exerciseTrackedStatus == NO_EXERCISE_IN_PROGRESS
    }

    private fun ExerciseClient.setExerciseUpdateCallback() {
        val callback = object : ExerciseUpdateCallback {
            override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
                // ExerciseUpdate doc ↓
                // https://developer.android.com/reference/androidx/health/services/client/data/ExerciseUpdate
                val latestMetrics = update.latestMetrics
                latestMetrics.getData(DataType.SPEED).forEach { speed ->
                    _current.update { it.copyActivity(speed = Activity.Speed(speed.value)) }
                }
                latestMetrics.getData(DataType.DISTANCE).forEach { distance ->
                    _current.update { it.copyActivity(distance = Activity.Distance(distance.value)) }
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
                    it.copy(measurementStatus = ActivityState.MeasurementStatus.LOCATION_ERROR)
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
                            it.copy(measurementStatus = ActivityState.MeasurementStatus.LOCATION_ERROR)
                        }
                    }

                    LocationAvailability.NO_GNSS -> {
                        _current.update {
                            it.copy(measurementStatus = ActivityState.MeasurementStatus.LOCATION_PERMISSTION_DENIED)
                        }
                    }

                    LocationAvailability.ACQUIRING -> {
                        _current.update {
                            it.copy(measurementStatus = ActivityState.MeasurementStatus.LOCATION_ACQUIRING)
                        }
                    }
                    LocationAvailability.ACQUIRED_TETHERED,
                    LocationAvailability.ACQUIRED_UNTETHERED -> {
                        _current.update {
                            it.copy(measurementStatus = ActivityState.MeasurementStatus.LOCATION_ACQUIRED)
                        }
                    }
                }
            }
        }
        this.setUpdateCallback(callback)
    }
}