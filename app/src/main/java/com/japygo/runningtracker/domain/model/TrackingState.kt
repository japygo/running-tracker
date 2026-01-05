package com.japygo.runningtracker.domain.model

import android.location.Location
import com.google.android.gms.maps.model.LatLng

data class TrackingState(
    val startTime: Long = 0,
    val endTime: Long = 0,
    val distance: Double = 0.0,
    val duration: Long = 0,
    val pathPoints: List<LatLng> = emptyList(),
    val lastLatLng: LatLng? = null,
    val isStarted: Boolean = false,
    val isPaused: Boolean = false,
    val lastLocation: Location? = null,
)
