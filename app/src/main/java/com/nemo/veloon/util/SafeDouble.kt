package com.nemo.veloon.util

class SafeDouble(
    private val value: Double
) {
    companion object {
        val ZERO = SafeDouble(0.0)

        fun Double.toSafe(): SafeDouble {
            return SafeDouble(this)
        }
    }

    /**
     * Doubleの異常値を正常値に変換した上で返す
     *
     * Double.POSITIVE_INFINITY -> Double.MAX_VALUE
     * Double.NEGATIVE_INFINITY -> Double.MIN_VALUE
     * Double.NaN -> 0.0
     *
     * @return 正常値 [Double]
     */
    fun getValue() = if (value == Double.POSITIVE_INFINITY) {
        Double.MAX_VALUE
    } else if (value.isNaN()) {
        0.0
    } else if (value == Double.NEGATIVE_INFINITY) {
        Double.MIN_VALUE
    } else {
        value
    }

    fun formattedContent(formatter: String): Double {
        return formatter.format(getValue()).toDouble()
    }
}
