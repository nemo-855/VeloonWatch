package com.nemo.veloon.domain

/**
 * 計測情報
 *
 * @param activity 走行情報
 * @param isLoading 計測情報の取得中かどうか
 * @param isAvailable 計測情報の取得が可能かどうか
 */
data class ActivityState(
    val activity: Activity,
    val isLoading: Boolean,
    val isAvailable: Boolean,
) {
    companion object {
        val INITIAL = ActivityState(
            activity = Activity.EMPTY,
            isLoading = false,
            isAvailable = false,
        )
    }

    fun copyActivity(
        pace: Activity.Pace,
    ): ActivityState {
        return copy(activity = activity.copy(pace = pace))
    }
}
