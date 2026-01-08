package com.japygo.runningtracker.domain.repository

import com.japygo.runningtracker.domain.model.BatteryState
import kotlinx.coroutines.flow.Flow

interface BatteryRepository {
    fun observeBatteryState(): Flow<BatteryState>
}