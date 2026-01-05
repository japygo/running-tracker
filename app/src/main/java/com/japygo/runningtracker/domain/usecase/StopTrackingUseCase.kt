package com.japygo.runningtracker.domain.usecase

import com.japygo.runningtracker.domain.repository.TrackingRepository
import javax.inject.Inject

class StopTrackingUseCase @Inject constructor(
    private val repository: TrackingRepository,
) {
    suspend operator fun invoke() {
        repository.stopTracking()
    }
}