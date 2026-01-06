package com.japygo.runningtracker.domain.controller

interface TrackingServiceController {
    fun startTracking()
    fun pauseTracking()
    fun resumeTracking()
    fun stopTracking()
}