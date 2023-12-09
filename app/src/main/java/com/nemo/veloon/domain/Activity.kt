package com.nemo.veloon.domain

/**
 * 走行情報
 *
 * @param pace 走行速度
 */
data class Activity(
    val pace: Pace,
    val distance: Distance,
) {
    companion object {
        val EMPTY = Activity(
            pace = Pace(Double.NaN),
            distance = Distance(Double.NaN),
        )
    }

    /**
     * 走行速度(km/h)
     */
    @JvmInline
    value class Pace(val value: Double)

    /**
     * 走行距離(km)
     */
    @JvmInline
    value class Distance(val value: Double)
}
