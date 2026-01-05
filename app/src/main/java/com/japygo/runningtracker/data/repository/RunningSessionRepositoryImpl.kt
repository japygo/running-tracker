package com.japygo.runningtracker.data.repository

import com.japygo.runningtracker.data.dao.RunningSessionDao
import com.japygo.runningtracker.data.mapper.toDomain
import com.japygo.runningtracker.data.mapper.toEntity
import com.japygo.runningtracker.domain.model.RunningSession
import com.japygo.runningtracker.domain.repository.RunningSessionRepository
import javax.inject.Inject

class RunningSessionRepositoryImpl @Inject constructor(
    private val dao: RunningSessionDao,
) : RunningSessionRepository {

    override suspend fun insert(runningSession: RunningSession) {
        dao.insert(runningSession.toEntity())
    }

    override suspend fun findAll(): List<RunningSession> {
        return dao.findAll().map { it.toDomain() }
    }
}