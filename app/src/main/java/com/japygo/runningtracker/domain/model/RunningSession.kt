package com.japygo.runningtracker.domain.model

import com.google.android.gms.maps.model.LatLng

data class RunningSession(
    val startTime: Long = 0,
    val endTime: Long = 0,
    val distance: Double = 0.0,
    val duration: Long = 0,
    val pathPoints: List<LatLng> = emptyList(),
)