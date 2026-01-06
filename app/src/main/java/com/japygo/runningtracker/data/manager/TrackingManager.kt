package com.japygo.runningtracker.data.manager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.japygo.runningtracker.domain.model.TrackingState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val _state = MutableStateFlow(TrackingState())
    val state = _state.asStateFlow()

    private var isTracking = false
    private var lastLocation: Location? = null

    fun startTracking() {
        if (isTracking) return
        isTracking = true

        _state.value = TrackingState(
            startTime = System.currentTimeMillis(),
            isStarted = true,
        )
        lastLocation = null

        requestLocationUpdates()
    }

    fun pauseTracking() {
        if (!isTracking) return
        isTracking = false

        _state.update {
            it.copy(
                isStarted = false,
                isPaused = true,
            )
        }

        removeLocationUpdates()
    }

    fun resumeTracking() {
        if (isTracking) return
        isTracking = true

        _state.update {
            it.copy(
                isStarted = true,
                isPaused = false,
            )
        }

        requestLocationUpdates()
    }

    fun stopTracking() {
        if (!isTracking && _state.value.distance == 0.0) return
        isTracking = false

        removeLocationUpdates()

        _state.update {
            it.copy(
                endTime = System.currentTimeMillis(),
            )
        }

        _state.value = TrackingState()
        lastLocation = null
    }

    fun restoreState(savedState: TrackingState) {
        _state.value = savedState

        if (savedState.pathPoints.isNotEmpty()) {
            val last = savedState.pathPoints.last()
            lastLocation = Location("").apply {
                latitude = last.first
                longitude = last.second
            }
        }

        if (savedState.isStarted) {
            isTracking = true
            requestLocationUpdates()
        }
    }

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper(),
        )
    }

    private fun removeLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.locations.forEach { location ->
                _state.update {
                    var distance = it.distance
                    lastLocation?.let { prevLoc ->
                        distance += prevLoc.distanceTo(location).toDouble()
                    }
                    lastLocation = location

                    val newState = it.copy(
                        pathPoints = it.pathPoints + Pair(location.latitude, location.longitude),
                        distance = distance,
                    )

                    newState
                }
            }
        }
    }
}