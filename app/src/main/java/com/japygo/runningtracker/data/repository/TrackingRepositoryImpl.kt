package com.japygo.runningtracker.data.repository

import android.content.Context
import android.content.Intent
import com.japygo.runningtracker.data.service.TrackingManager
import com.japygo.runningtracker.data.service.TrackingService
import com.japygo.runningtracker.domain.model.TrackingState
import com.japygo.runningtracker.domain.repository.TrackingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TrackingRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val trackingManager: TrackingManager,
) : TrackingRepository {

    override suspend fun startTracking() {
        val intent = Intent(context, TrackingService::class.java).apply {
            action = TrackingService.ACTION_START
        }
        context.startForegroundService(intent)
    }

    override suspend fun pauseTracking() {
        val intent = Intent(context, TrackingService::class.java).apply {
            action = TrackingService.ACTION_PAUSE
        }
        context.startService(intent)
    }

    override suspend fun resumeTracking() {
        val intent = Intent(context, TrackingService::class.java).apply {
            action = TrackingService.ACTION_RESUME
        }
        context.startService(intent)
    }

    override suspend fun stopTracking() {
        val intent = Intent(context, TrackingService::class.java).apply {
            action = TrackingService.ACTION_STOP
        }
        context.startService(intent)
    }

    override fun observeTrackingState(): Flow<TrackingState> {
        return trackingManager.state
    }
}