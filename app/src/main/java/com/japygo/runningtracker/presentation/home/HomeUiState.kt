package com.japygo.runningtracker.presentation.home

import com.japygo.runningtracker.domain.model.BatteryState
import com.japygo.runningtracker.domain.model.RunningSession

data class HomeUiState(
    val isStarted: Boolean = false,
    val isPaused: Boolean = false,
    val distance: Double = 0.0,
    val duration: Long = 0L,
    val pathPoints: List<Pair<Double, Double>> = emptyList(),
    val startTime: Long = 0,
    val currentLocation: Pair<Double, Double>? = null,
    val isGpsAvailable: Boolean = true,
    val batteryStatus: BatteryState.Status = BatteryState.Status.OK,
    val runningSessions: List<RunningSession> = emptyList(),
)
