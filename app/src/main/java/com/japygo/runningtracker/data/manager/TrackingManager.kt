package com.japygo.runningtracker.data.manager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
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

    private val locationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private val _state = MutableStateFlow(TrackingState())
    val state = _state.asStateFlow()

    private var isTracking = false
    private var lastLocation: Location? = null
    
    // 콜백 인스턴스를 멤버 변수로 유지 (중요!)
    private val locationCallback = TrackingLocationCallback()

    init {
        // 앱 시작 시 위치 요청 (지도 표시용)
        requestLocationUpdates()
        checkGpsAvailability()
    }

    fun startTracking() {
        if (isTracking) return
        isTracking = true

        _state.value = TrackingState(
            startTime = System.currentTimeMillis(),
            isStarted = true,
            isGpsAvailable = checkGpsAvailability(),
        )
        lastLocation = null
        
        // 확실하게 업데이트 다시 요청 (혹시 멈췄을 경우 대비)
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
    }

    fun stopTracking() {
        if (!isTracking && _state.value.distance == 0.0) return
        isTracking = false
        
        // 위치 업데이트 중지 (배터리 절약 및 좀비 콜백 방지)
        stopLocationUpdates()

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

    private fun checkGpsAvailability(): Boolean {
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        
        val isAvailable = isGpsEnabled || isNetworkEnabled
        
        _state.update {
            it.copy(isGpsAvailable = isAvailable)
        }
        
        return isAvailable
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
        
        // 중복 요청 방지를 위해 먼저 제거
        fusedLocationClient.removeLocationUpdates(locationCallback)

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback, // 멤버 변수 사용
            Looper.getMainLooper(),
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback) // 멤버 변수 사용
    }

    private inner class TrackingLocationCallback : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            checkGpsAvailability()
            
            result.locations.forEach { location ->
                updateCurrentLocation(location)

                if (isTracking) {
                    recordLocation(location)
                }
            }
        }

        private fun updateCurrentLocation(location: Location) {
            _state.update {
                it.copy(currentLocation = Pair(location.latitude, location.longitude))
            }
        }

        private fun recordLocation(location: Location) {
            _state.update {
                var distance = it.distance
                lastLocation?.let { prevLoc ->
                    distance += prevLoc.distanceTo(location).toDouble()
                }
                lastLocation = location

                it.copy(
                    pathPoints = it.pathPoints + Pair(location.latitude, location.longitude),
                    distance = distance,
                )
            }
        }
    }
}