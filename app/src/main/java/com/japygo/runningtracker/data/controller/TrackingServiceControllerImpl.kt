package com.japygo.runningtracker.data.controller

import android.content.Context
import android.content.Intent
import com.japygo.runningtracker.data.service.TrackingService
import com.japygo.runningtracker.domain.controller.TrackingServiceController
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TrackingServiceControllerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : TrackingServiceController {

    override fun startTracking() {
        val intent = Intent(context, TrackingService::class.java).apply {
            action = TrackingService.ACTION_START
        }
        context.startForegroundService(intent)
    }

    override fun pauseTracking() {
        val intent = Intent(context, TrackingService::class.java).apply {
            action = TrackingService.ACTION_PAUSE
        }
        context.startService(intent)
    }

    override fun resumeTracking() {
        val intent = Intent(context, TrackingService::class.java).apply {
            action = TrackingService.ACTION_RESUME
        }
        context.startService(intent)
    }

    override fun stopTracking() {
        val intent = Intent(context, TrackingService::class.java).apply {
            action = TrackingService.ACTION_STOP
        }
        context.startService(intent)
    }
}