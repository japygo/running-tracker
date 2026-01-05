package com.japygo.runningtracker.domain.repository

import com.japygo.runningtracker.domain.model.RunningSession

interface RunningSessionRepository {
    suspend fun insert(runningSession: RunningSession)
    suspend fun findAll(): List<RunningSession>
}