package com.japygo.runningtracker.data.manager

import com.japygo.runningtracker.domain.model.BatteryState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BatteryStateManager @Inject constructor() {

    private val _state = MutableStateFlow(BatteryState())
    val state = _state.asStateFlow()

    fun updateBatteryState(percentage: Float) {
        _state.update {
            val status = when {
                percentage <= BatteryState.DANGER_THRESHOLD -> BatteryState.Status.DANGER
                percentage <= BatteryState.WARNING_THRESHOLD -> BatteryState.Status.WARNING
                else -> BatteryState.Status.OK
            }
            it.copy(
                percentage = percentage,
                status = status,
            )
        }
    }
}