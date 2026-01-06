package com.japygo.runningtracker.data.mapper

import com.japygo.runningtracker.data.entity.RunningSessionEntity
import com.japygo.runningtracker.domain.model.RunningSession
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

fun RunningSession.toEntity(): RunningSessionEntity {
    val pathPointsJson = json.encodeToString(pathPoints)

    return RunningSessionEntity(
        startTime = startTime,
        endTime = endTime,
        distance = distance,
        duration = duration,
        pathPointsJson = pathPointsJson,
    )
}

fun RunningSessionEntity.toDomain(): RunningSession {
    val pathPoints = if (pathPointsJson.isNotEmpty()) {
        try {
            json.decodeFromString<List<Pair<Double, Double>>>(pathPointsJson)
        } catch (_: Exception) {
            emptyList()
        }
    } else {
        emptyList()
    }

    return RunningSession(
        startTime = startTime,
        endTime = endTime,
        distance = distance,
        duration = duration,
        pathPoints = pathPoints,
    )
}