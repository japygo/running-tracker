package com.japygo.runningtracker.domain.repository

import com.japygo.runningtracker.domain.model.TrackingState
import kotlinx.coroutines.flow.Flow

interface TrackingRepository {
    fun observeTrackingState(): Flow<TrackingState>
    suspend fun saveTrackingState(state: TrackingState)
    suspend fun getTrackingState(): TrackingState?
    suspend fun clearTrackingState()
}