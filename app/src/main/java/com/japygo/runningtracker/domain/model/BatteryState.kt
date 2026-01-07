package com.japygo.runningtracker.domain.model

data class BatteryState(
    val percentage: Float = 100f,
    val status: Status = Status.OK,
) {
    enum class Status {
        OK, WARNING, DANGER
    }

    companion object {
        const val WARNING_THRESHOLD = 30
        const val DANGER_THRESHOLD = 20
    }
}
