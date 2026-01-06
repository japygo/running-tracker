package com.japygo.runningtracker.data.repository

import com.japygo.runningtracker.data.datastore.TrackingDataStore
import com.japygo.runningtracker.data.manager.TrackingManager
import com.japygo.runningtracker.domain.model.TrackingState
import com.japygo.runningtracker.domain.repository.TrackingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TrackingRepositoryImpl @Inject constructor(
    private val trackingManager: TrackingManager,
    private val trackingDataStore: TrackingDataStore,
) : TrackingRepository {

    override fun observeTrackingState(): Flow<TrackingState> {
        return trackingManager.state
    }

    override suspend fun saveTrackingState(state: TrackingState) {
        trackingDataStore.saveTrackingState(state)
    }

    override suspend fun getTrackingState(): TrackingState? {
        return trackingDataStore.getTrackingState()
    }

    override suspend fun clearTrackingState() {
        trackingDataStore.clearTrackingState()
    }
}