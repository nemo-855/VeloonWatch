package com.nemo.veloon.data.foregroundservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.nemo.veloon.data.repository.BikingActivityRepository
import com.nemo.veloon.data.sensor.ActivitySensor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ActivityService @Inject constructor(
    private val bikingActivityRepository: BikingActivityRepository,
    private val activitySensor: ActivitySensor,
) : Service() {
    private val activitySensorExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable !is ActivitySensor.ActivitySensorException) return@CoroutineExceptionHandler
        when (throwable) {
            ActivitySensor.ActivitySensorException.AnotherExerciseIsInProgress -> {
                // TODO Implement
            }

            ActivitySensor.ActivitySensorException.BikingMeasurementIsNotSupported -> {
                // TODO Implement
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(activitySensorExceptionHandler).launch {
            bikingActivityRepository.setIsBiking(true)
            activitySensor.start()
            activitySensor.current.collect {
                bikingActivityRepository.setBikingPace(it.activity.pace)
                bikingActivityRepository.setBikingDistance(it.activity.distance)
            }
        }
        return START_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        CoroutineScope(activitySensorExceptionHandler).launch {
            bikingActivityRepository.setIsBiking(false)
            activitySensor.stop()
        }
        return super.stopService(name)
    }
}