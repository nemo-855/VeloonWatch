package com.nemo.veloon.data.repository

import com.nemo.veloon.data.datastore.BikingActivityDataStore
import com.nemo.veloon.domain.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BikingActivityRepository @Inject constructor(
    private val bikingActivityDataStore: BikingActivityDataStore
) {
    val isBikingFlow: Flow<Boolean> = bikingActivityDataStore.isBikingFlow

    suspend fun setIsBiking(isBiking: Boolean) =
        bikingActivityDataStore.setIsBiking(isBiking)

    val bikingPaceFlow: Flow<Activity.Pace> = bikingActivityDataStore.bikingPaceFlow.map { Activity.Pace(it) }

    suspend fun setBikingPace(bikingPace: Activity.Pace) =
        bikingActivityDataStore.setBikingPace(bikingPace.value)

    val bikingDistanceFlow: Flow<Activity.Distance> = bikingActivityDataStore.bikingDistanceFlow.map { Activity.Distance(it) }

    suspend fun setBikingDistance(bikingDistance: Activity.Distance) =
        bikingActivityDataStore.setBikingDistance(bikingDistance.value)
}