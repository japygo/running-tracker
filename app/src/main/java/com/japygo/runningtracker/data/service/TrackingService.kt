package com.japygo.runningtracker.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.japygo.runningtracker.data.manager.TrackingManager
import com.japygo.runningtracker.domain.repository.TrackingRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    @Inject
    lateinit var trackingManager: TrackingManager

    @Inject
    lateinit var trackingRepository: TrackingRepository

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        lifecycleScope.launch {
            val savedState = trackingRepository.getTrackingState()
            if (savedState != null) {
                trackingManager.restoreState(savedState)
            }
        }

        lifecycleScope.launch {
            trackingManager.state
                .drop(1)
                .collect { trackingRepository.saveTrackingState(it) }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_START -> startTracking()
            ACTION_PAUSE -> pauseTracking()
            ACTION_RESUME -> resumeTracking()
            ACTION_STOP -> stopTracking()
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Running Tracker Channel",
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Running Tracker")
            .setContentText("운동중")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .build()
    }

    private fun startTracking() {
        startForeground(NOTIFICATION_ID, createNotification())
        trackingManager.startTracking()
    }

    private fun pauseTracking() {
        trackingManager.pauseTracking()
    }

    private fun resumeTracking() {
        trackingManager.resumeTracking()
    }

    private fun stopTracking() {
        lifecycleScope.launch {
            trackingRepository.clearTrackingState()
        }
        trackingManager.stopTracking()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    companion object {
        const val CHANNEL_ID = "RunningForegroundService"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
    }
}