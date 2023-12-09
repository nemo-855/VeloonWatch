package com.nemo.veloon.data.sensor

import android.content.Context
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.MeasureClient
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.getCapabilities
import androidx.health.services.client.unregisterMeasureCallback
import com.nemo.veloon.domain.Activity
import com.nemo.veloon.domain.ActivityState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

class ActivitySensorImpl(context: Context) : ActivitySensor {
    private val measureClient = HealthServices.getClient(context).measureClient

    private val _current = MutableStateFlow(ActivityState.INITIAL)
    override val current = _current.asSharedFlow()

    private val paceCallback = object : MeasureCallback {
        override fun onAvailabilityChanged(
            dataType: DeltaDataType<*, *>,
            availability: Availability
        ) {
            when (availability) {
                DataTypeAvailability.UNAVAILABLE,
                DataTypeAvailability.UNKNOWN -> {
                    _current.tryEmit(_current.value.copy(isAvailable = false))
                }

                DataTypeAvailability.AVAILABLE -> {
                    _current.tryEmit(_current.value.copy(isAvailable = true))
                }
            }
        }

        override fun onDataReceived(data: DataPointContainer) {
            data.getData(DataType.PACE).forEach { pace ->
                _current.tryEmit(
                    _current.value.copyActivity(pace = Activity.Pace(pace.value))
                )
            }
        }
    }

    override suspend fun start() {
        if (!isPaceSupported(measureClient)) return
        measureClient.registerMeasureCallback(DataType.PACE, paceCallback)
    }

    override suspend fun stop() {
        measureClient.unregisterMeasureCallback(DataType.PACE, paceCallback)
    }


    private suspend fun isPaceSupported(measureClient: MeasureClient): Boolean {
        val capabilities = measureClient.getCapabilities()
        return DataType.PACE in capabilities.supportedDataTypesMeasure
    }
}