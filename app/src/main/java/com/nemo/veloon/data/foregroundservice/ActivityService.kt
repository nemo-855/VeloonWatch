package com.nemo.veloon.data.foregroundservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.nemo.veloon.data.sensor.ActivitySensor
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ActivityService @Inject constructor(
    private val activitySensor: ActivitySensor
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
            activitySensor.start()
        }
        return START_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        CoroutineScope(activitySensorExceptionHandler).launch {
            activitySensor.stop()
        }
        return super.stopService(name)
    }
}