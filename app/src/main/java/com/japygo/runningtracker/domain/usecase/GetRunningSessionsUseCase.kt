package com.japygo.runningtracker.domain.usecase

import com.japygo.runningtracker.domain.model.RunningSession
import com.japygo.runningtracker.domain.repository.RunningSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRunningSessionsUseCase @Inject constructor(
    private val repository: RunningSessionRepository,
) {
    operator fun invoke(): Flow<List<RunningSession>> {
        return repository.findAll()
    }
}