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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class ActivitySensorImpl @Inject constructor(@ApplicationContext context: Context) : ActivitySensor {
    private val measureClient = HealthServices.getClient(context).measureClient

    private val _current = MutableStateFlow(ActivityState.INITIAL)
    override val current = _current.asStateFlow()

    private val paceCallback = object : MeasureCallback {
        override fun onAvailabilityChanged(
            dataType: DeltaDataType<*, *>,
            availability: Availability
        ) {
            when (availability) {
                DataTypeAvailability.UNAVAILABLE,
                DataTypeAvailability.UNKNOWN -> {
                    _current.update { it.copy(isAvailable = false) }
                }

                DataTypeAvailability.AVAILABLE -> {
                    _current.update { it.copy(isAvailable = true) }
                }
            }
        }

        override fun onDataReceived(data: DataPointContainer) {
            data.getData(DataType.PACE).forEach { pace ->
                _current.update { it.copyActivity(pace = Activity.Pace(pace.value)) }
            }
        }
    }

    override suspend fun start() {
        if (!isPaceSupported(measureClient)) return
        measureClient.registerMeasureCallback(DataType.PACE, paceCallback)
    }

    override suspend fun stop() {
        val notRegisteredCallbackExceptionHandler = CoroutineExceptionHandler { _, e ->
            // 未登録のHandlerを削除しようとした時の例外は無視する
            if (e !is IllegalArgumentException) throw e
        }
        CoroutineScope(notRegisteredCallbackExceptionHandler).launch {
            _current.update { it.copy(activity = Activity.EMPTY) }
            measureClient.unregisterMeasureCallback(DataType.PACE, paceCallback)
        }
    }


    private suspend fun isPaceSupported(measureClient: MeasureClient): Boolean {
        val capabilities = measureClient.getCapabilities()
        return DataType.PACE in capabilities.supportedDataTypesMeasure
    }
}