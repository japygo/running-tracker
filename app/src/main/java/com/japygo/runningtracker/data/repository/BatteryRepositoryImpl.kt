package com.japygo.runningtracker.data.repository

import com.japygo.runningtracker.data.manager.BatteryStateManager
import com.japygo.runningtracker.domain.model.BatteryState
import com.japygo.runningtracker.domain.repository.BatteryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BatteryRepositoryImpl @Inject constructor(
    private val batteryStateManager: BatteryStateManager,
) : BatteryRepository {

    override fun observeBatteryState(): Flow<BatteryState> {
        return batteryStateManager.state
    }
}