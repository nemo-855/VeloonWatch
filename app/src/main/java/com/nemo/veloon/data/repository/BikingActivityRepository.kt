package com.nemo.veloon.data.repository

import com.nemo.veloon.data.datastore.BikingActivityDataStore
import com.nemo.veloon.data.datastore.SerializableActivityState.Companion.toActivityStateOrInitial
import com.nemo.veloon.data.sensor.ActivitySensor
import com.nemo.veloon.domain.ActivityState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class BikingActivityRepository @Inject constructor(
    private val bikingActivityDataStore: BikingActivityDataStore,
    private val activitySensor: ActivitySensor,
) {
    val isBikingFlow: Flow<Boolean> = bikingActivityDataStore.isBikingFlow

    val bikingActivityStateFlow: Flow<ActivityState> =
        bikingActivityDataStore.bikingActivityStateFlow.map { it.toActivityStateOrInitial() }

    private var collectActivitySensorCurrentStateJob: Job? = null

    suspend fun startBiking() {
        collectActivitySensorCurrentStateJob = CoroutineScope(
            CoroutineExceptionHandler { _, throwable ->
                throw throwable // ハンドリングは上のレイヤーに任せる
            }
        ).launch {
            activitySensor.current.collect {
                bikingActivityDataStore.setActivityState(it)
                if (it.measurementStatus == ActivityState.MeasurementStatus.FINISHED) {
                    resetActivityState()
                }
            }
        }
        collectActivitySensorCurrentStateJob?.start()

        bikingActivityDataStore.setIsBiking(true)
        activitySensor.start()
    }

    suspend fun finishBiking() {
        activitySensor.stop()

        collectActivitySensorCurrentStateJob?.cancel()
        collectActivitySensorCurrentStateJob = null
    }

    private suspend fun resetActivityState() {
        bikingActivityDataStore.setIsBiking(false)
        bikingActivityDataStore.deleteActivityState()
    }
}