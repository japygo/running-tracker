package com.japygo.runningtracker.data.repository

import com.japygo.runningtracker.data.dao.RunningSessionDao
import com.japygo.runningtracker.data.mapper.toDomain
import com.japygo.runningtracker.data.mapper.toEntity
import com.japygo.runningtracker.domain.model.RunningSession
import com.japygo.runningtracker.domain.repository.RunningSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RunningSessionRepositoryImpl @Inject constructor(
    private val dao: RunningSessionDao,
) : RunningSessionRepository {

    override suspend fun insert(runningSession: RunningSession) {
        dao.insert(runningSession.toEntity())
    }

    override fun findAll(): Flow<List<RunningSession>> {
        return dao.findAll().map { list -> list.map { it.toDomain() } }
    }
}