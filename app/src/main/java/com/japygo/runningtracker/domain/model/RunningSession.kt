package com.japygo.runningtracker.domain.model

data class RunningSession(
    val id: Long = 0,
    val startTime: Long = 0,
    val endTime: Long = 0,
    val distance: Double = 0.0,
    val duration: Long = 0,
    val pathPoints: List<Pair<Double, Double>> = emptyList(),
)