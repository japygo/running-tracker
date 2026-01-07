package com.japygo.runningtracker.domain.repository

import com.japygo.runningtracker.domain.model.RunningSession
import kotlinx.coroutines.flow.Flow

interface RunningSessionRepository {
    suspend fun insert(runningSession: RunningSession)
    fun findAll(): Flow<List<RunningSession>>
}