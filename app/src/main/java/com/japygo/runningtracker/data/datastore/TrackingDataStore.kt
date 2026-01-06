package com.japygo.runningtracker.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.japygo.runningtracker.domain.model.TrackingState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val dataStore = context.dataStore
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun saveTrackingState(state: TrackingState) {
        dataStore.edit { preferences ->
            preferences[TRACKING_STATE_KEY] = json.encodeToString(state)
        }
    }

    suspend fun getTrackingState(): TrackingState? {
        val preferences = dataStore.data.first()
        val stateJson = preferences[TRACKING_STATE_KEY] ?: return null
        return try {
            json.decodeFromString<TrackingState>(stateJson)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun clearTrackingState() {
        dataStore.edit { preferences ->
            preferences.remove(TRACKING_STATE_KEY)
        }
    }

    companion object {
        private val Context.dataStore by preferencesDataStore("tracking_prefs")
        private val TRACKING_STATE_KEY = stringPreferencesKey("tracking_state")
    }
}