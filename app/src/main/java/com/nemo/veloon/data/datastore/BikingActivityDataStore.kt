package com.nemo.veloon.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BikingActivityDataStore(
    private val context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = BIKING_ACTIVITY_DATASTORE_NAME
    )

    val bikingPaceFlow: Flow<Double> = context.dataStore.data.map {
        it[BIKING_PACE_KEY] ?: 0.0
    }

    suspend fun setBikingPace(bikingPace: Double) {
        context.dataStore.edit {
            it[BIKING_PACE_KEY] = bikingPace
        }
    }

    val bikingDistanceFlow: Flow<Double> = context.dataStore.data.map {
        it[BIKING_DISTANCE_KEY] ?: 0.0
    }

    suspend fun setBikingDistance(bikingDistance: Double) {
        context.dataStore.edit {
            it[BIKING_DISTANCE_KEY] = bikingDistance
        }
    }

    companion object {
        private const val BIKING_ACTIVITY_DATASTORE_NAME = "biking_activity_datastore"

        private val BIKING_PACE_KEY = doublePreferencesKey("biking_pace")
        private val BIKING_DISTANCE_KEY = doublePreferencesKey("biking_distance")
    }
}