package com.japygo.runningtracker.domain.usecase

import com.japygo.runningtracker.domain.model.RunningSession
import com.japygo.runningtracker.domain.repository.RunningSessionRepository
import javax.inject.Inject

class SaveRunningSessionUseCase @Inject constructor(
    private val repository: RunningSessionRepository,
) {
    suspend operator fun invoke(runningSession: RunningSession) {
        repository.insert(runningSession)
    }
}