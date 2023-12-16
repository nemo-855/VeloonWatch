package com.nemo.veloon.domain

import com.nemo.veloon.util.SafeDouble

/**
 * 走行情報
 *
 * @param averageSpeed 走行速度
 * @param maxSpeed 最高の走行速度
 * @param distance 走行距離
 * @param calories 消費カロリー
 */
data class Activity(
    val averageSpeed: Speed,
    val maxSpeed: Speed,
    val distance: Distance,
    val calories: Calories,
) {
    companion object {
        val EMPTY = Activity(
            averageSpeed = Speed(SafeDouble.ZERO),
            maxSpeed = Speed(SafeDouble.ZERO),
            distance = Distance(SafeDouble.ZERO),
            calories = Calories(SafeDouble.ZERO),
        )
    }

    /**
     * 走行速度(km/h)
     */
    @JvmInline
    value class Speed(val value: SafeDouble)

    /**
     * 走行距離(km)
     */
    @JvmInline
    value class Distance(val value: SafeDouble)

    /**
     * 消費カロリー(kcal)
     */
    @JvmInline
    value class Calories(val value: SafeDouble)
}
