package com.nemo.veloon.data.sensor

import com.nemo.veloon.domain.ActivityState
import kotlinx.coroutines.flow.StateFlow

/**
 * 計測情報を提供するためのインターフェース
 */
interface ActivitySensor {
    /**
     * 計測情報を開始する
     */
    suspend fun start()

    /**
     * 計測情報を停止する
     */
    suspend fun stop()

    /**
     * 現在の計測情報
     */
    val current: StateFlow<ActivityState>
}
