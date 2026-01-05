package com.japygo.runningtracker.data.mapper

import com.japygo.runningtracker.data.entity.RunningSessionEntity
import com.japygo.runningtracker.domain.model.RunningSession

fun RunningSession.toEntity(): RunningSessionEntity {
    return RunningSessionEntity(
        startTime = startTime,
        endTime = endTime,
        distance = distance,
        duration = duration,
    )
}

fun RunningSessionEntity.toDomain(): RunningSession {
    return RunningSession(
        startTime = startTime,
        endTime = endTime,
        distance = distance,
        duration = duration,
    )
}