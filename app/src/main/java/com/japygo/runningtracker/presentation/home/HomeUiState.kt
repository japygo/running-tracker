package com.japygo.runningtracker.presentation.home

import com.google.android.gms.maps.model.LatLng

data class HomeUiState(
    val isStarted: Boolean = false,
    val isPaused: Boolean = false,
    val distance: Double = 0.0,
    val duration: Long = 0L,
    val pathPoints: List<LatLng> = emptyList(),
)
