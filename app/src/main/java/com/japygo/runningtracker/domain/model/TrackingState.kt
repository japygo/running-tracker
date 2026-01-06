package com.japygo.runningtracker.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TrackingState(
    val startTime: Long = 0,
    val endTime: Long = 0,
    val distance: Double = 0.0,
    val duration: Long = 0,
    val pathPoints: List<Pair<Double, Double>> = emptyList(),
    val isStarted: Boolean = false,
    val isPaused: Boolean = false,
)
