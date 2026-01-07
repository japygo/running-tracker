package com.japygo.runningtracker.domain.usecase

import com.japygo.runningtracker.domain.model.BatteryState
import com.japygo.runningtracker.domain.repository.BatteryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBatteryStateUseCase @Inject constructor(
    private val repository: BatteryRepository,
) {
    operator fun invoke(): Flow<BatteryState> {
        return repository.observeBatteryState()
    }
}