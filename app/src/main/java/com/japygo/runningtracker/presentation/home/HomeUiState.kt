package com.japygo.runningtracker.presentation.home

data class HomeUiState(
    val isStarted: Boolean = false,
    val isPaused: Boolean = false,
    val distance: Double = 0.0,
    val duration: Long = 0L,
    val pathPoints: List<Pair<Double, Double>> = emptyList(),
    val startTime: Long = 0,
)
