package com.japygo.runningtracker.domain.usecase

import com.japygo.runningtracker.domain.controller.TrackingServiceController
import javax.inject.Inject

class StopTrackingUseCase @Inject constructor(
    private val controller: TrackingServiceController,
) {
    operator fun invoke() {
        controller.stopTracking()
    }
}