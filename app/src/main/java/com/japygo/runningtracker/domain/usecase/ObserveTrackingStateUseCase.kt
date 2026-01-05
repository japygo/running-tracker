package com.japygo.runningtracker.domain.usecase

import com.japygo.runningtracker.domain.model.TrackingState
import com.japygo.runningtracker.domain.repository.TrackingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTrackingStateUseCase @Inject constructor(
    private val repository: TrackingRepository,
) {
    operator fun invoke(): Flow<TrackingState> {
        return repository.observeTrackingState()
    }
}