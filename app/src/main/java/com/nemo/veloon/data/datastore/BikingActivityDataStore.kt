package com.nemo.veloon.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nemo.veloon.domain.ActivityState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BikingActivityDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val BIKING_ACTIVITY_DATASTORE_NAME = "biking_activity_datastore"

        private val IS_BIKING_KEY = booleanPreferencesKey("is_biking")
        private val BIKING_ACTIVITY_STATE_KEY = stringPreferencesKey("biking_activity_state")

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

    val bikingActivityStateFlow: Flow<SerializableActivityState?> = context.dataStore.data.map { it ->
        it[BIKING_ACTIVITY_STATE_KEY]?.let { savedString ->
            Json.decodeFromString<SerializableActivityState>(savedString)
        }
    }

    suspend fun setActivityState(activityState: ActivityState) {
        val serializableActivityState = SerializableActivityState.from(activityState)
        context.dataStore.edit {
            it[BIKING_ACTIVITY_STATE_KEY] = Json.encodeToString(serializableActivityState)
        }
    }

    suspend fun deleteActivityState() {
        context.dataStore.edit {
            it.remove(BIKING_ACTIVITY_STATE_KEY)
        }
    }
}