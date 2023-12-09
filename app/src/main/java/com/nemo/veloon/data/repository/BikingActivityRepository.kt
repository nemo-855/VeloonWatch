package com.nemo.veloon.data.repository

import com.nemo.veloon.data.datastore.BikingActivityDataStore
import com.nemo.veloon.domain.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BikingActivityRepository @Inject constructor(
    private val bikingActivityDataStore: BikingActivityDataStore
) {
    val activeBikingPaceFlow: Flow<Activity.Pace> = bikingActivityDataStore.bikingPaceFlow.map { Activity.Pace(it) }

    suspend fun setActiveBikingPace(bikingPace: Activity.Pace) =
        bikingActivityDataStore.setBikingPace(bikingPace.value)

    val activeBikingDistanceFlow: Flow<Activity.Distance> = bikingActivityDataStore.bikingDistanceFlow.map { Activity.Distance(it) }

    suspend fun setActiveBikingDistance(bikingDistance: Activity.Distance) =
        bikingActivityDataStore.setBikingDistance(bikingDistance.value)
}