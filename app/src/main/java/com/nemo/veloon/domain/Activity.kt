package com.nemo.veloon.domain

/**
 * 走行情報
 *
 * @param speed 走行速度
 */
data class Activity(
    val speed: Speed,
    val distance: Distance,
) {
    companion object {
        val EMPTY = Activity(
            speed = Speed(0.0),
            distance = Distance(0.0),
        )
    }

    /**
     * 走行速度(km/h)
     */
    @JvmInline
    value class Speed(val value: Double)

    /**
     * 走行距離(km)
     */
    @JvmInline
    value class Distance(val value: Double)
}
