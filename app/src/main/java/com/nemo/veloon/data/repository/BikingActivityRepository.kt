package com.nemo.veloon.data.repository

import com.nemo.veloon.data.datastore.BikingActivityDataStore
import com.nemo.veloon.data.datastore.SerializableActivityState.Companion.toActivityStateOrInitial
import com.nemo.veloon.domain.ActivityState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BikingActivityRepository @Inject constructor(
    private val bikingActivityDataStore: BikingActivityDataStore,
) {
    val isBikingFlow: Flow<Boolean> = bikingActivityDataStore.isBikingFlow

    val bikingActivityStateFlow: Flow<ActivityState> =
        bikingActivityDataStore.bikingActivityStateFlow.map { it.toActivityStateOrInitial() }

    suspend fun startBiking() {
        bikingActivityDataStore.setIsBiking(true)
    }

    suspend fun finishBiking() {
        bikingActivityDataStore.setIsBiking(false)
        bikingActivityDataStore.deleteActivityState()
    }

    suspend fun setActivityState(activityState: ActivityState) {
        bikingActivityDataStore.setActivityState(activityState)
    }
}