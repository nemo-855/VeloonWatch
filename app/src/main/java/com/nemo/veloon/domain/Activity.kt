package com.nemo.veloon.domain

/**
 * 走行情報
 *
 * @param pace 走行速度
 */
data class Activity(
    val pace: Pace,
) {
    companion object {
        val EMPTY = Activity(Pace(Double.NaN))
    }

    /**
     * 走行速度(km/h)
     */
    @JvmInline
    value class Pace(val value: Double)
}
