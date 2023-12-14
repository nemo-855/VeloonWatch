package com.nemo.veloon.domain

/**
 * 走行情報
 *
 * @param averageSpeed 走行速度
 * @param maxSpeed 最高の走行速度
 * @param distance 走行距離
 */
data class Activity(
    val averageSpeed: Speed,
    val maxSpeed: Speed,
    val distance: Distance,
) {
    companion object {
        val EMPTY = Activity(
            averageSpeed = Speed(0.0),
            maxSpeed = Speed(0.0),
            distance = Distance(0.0),
        )
    }

    /**
     * 走行速度(m/s)
     */
    @JvmInline
    value class Speed(val value: Double)

    /**
     * 走行距離(m)
     */
    @JvmInline
    value class Distance(val value: Double)
}
