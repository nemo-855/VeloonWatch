package com.nemo.veloon.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BikingActivityDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val BIKING_ACTIVITY_DATASTORE_NAME = "biking_activity_datastore"

        private val IS_BIKING_KEY = booleanPreferencesKey("is_biking")
        private val BIKING_PACE_KEY = doublePreferencesKey("biking_pace")
        private val BIKING_DISTANCE_KEY = doublePreferencesKey("biking_distance")

        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = BIKING_ACTIVITY_DATASTORE_NAME
        )
    }

    val isBikingFlow: Flow<Boolean> = context.dataStore.data.map {
        it[IS_BIKING_KEY] ?: false
    }

    suspend fun setIsBiking(isBiking: Boolean) {
        context.dataStore.edit {
            it[IS_BIKING_KEY] = isBiking
        }
    }

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
}