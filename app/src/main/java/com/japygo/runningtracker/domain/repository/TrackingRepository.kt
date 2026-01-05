package com.japygo.runningtracker.domain.repository

import com.japygo.runningtracker.domain.model.TrackingState
import kotlinx.coroutines.flow.Flow

interface TrackingRepository {
    suspend fun startTracking()
    suspend fun pauseTracking()
    suspend fun resumeTracking()
    suspend fun stopTracking()
    fun observeTrackingState(): Flow<TrackingState>
}