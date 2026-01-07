package com.japygo.runningtracker.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.japygo.runningtracker.domain.model.BatteryState
import com.japygo.runningtracker.domain.model.BatteryState.Status.DANGER
import com.japygo.runningtracker.domain.model.BatteryState.Status.OK
import com.japygo.runningtracker.domain.model.BatteryState.Status.WARNING
import com.japygo.runningtracker.domain.model.RunningSession
import com.japygo.runningtracker.domain.usecase.GetRunningSessionsUseCase
import com.japygo.runningtracker.domain.usecase.ObserveBatteryStateUseCase
import com.japygo.runningtracker.domain.usecase.ObserveTrackingStateUseCase
import com.japygo.runningtracker.domain.usecase.PauseTrackingUseCase
import com.japygo.runningtracker.domain.usecase.ResumeTrackingUseCase
import com.japygo.runningtracker.domain.usecase.SaveRunningSessionUseCase
import com.japygo.runningtracker.domain.usecase.StartTrackingUseCase
import com.japygo.runningtracker.domain.usecase.StopTrackingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val startTrackingUseCase: StartTrackingUseCase,
    private val pauseTrackingUseCase: PauseTrackingUseCase,
    private val resumeTrackingUseCase: ResumeTrackingUseCase,
    private val stopTrackingUseCase: StopTrackingUseCase,
    private val observeTrackingStateUseCase: ObserveTrackingStateUseCase,
    private val saveRunningSessionUseCase: SaveRunningSessionUseCase,
    private val observeBatteryStateUseCase: ObserveBatteryStateUseCase,
    private val getRunningSessionsUseCase: GetRunningSessionsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<HomeEvent>()
    val event = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            observeTrackingStateUseCase().collect { state ->
                _uiState.update {
                    it.copy(
                        distance = state.distance,
                        duration = state.duration,
                        pathPoints = state.pathPoints,
                        startTime = state.startTime,
                        currentLocation = state.currentLocation,
                        isGpsAvailable = state.isGpsAvailable,
                    )
                }
            }
        }

        viewModelScope.launch {
            getRunningSessionsUseCase().collect { sessions ->
                _uiState.update {
                    it.copy(runningSessions = sessions)
                }
            }
        }

        viewModelScope.launch {
            observeBatteryStateUseCase()
                .distinctUntilChanged { old, new -> old.status == new.status }
                .collect { state ->
                    when (state.status) {
                        OK -> {}
                        WARNING -> {
                            showSnackbar("배터리 잔량이 30% 이하입니다")
                        }
                        DANGER -> {
                            showSnackbar("배터리 잔량이 부족하여 운동 종료합니다")
                            stop()
                        }
                    }
                    updateBatteryStatus(state.status)
                }
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnStart -> start()
            HomeAction.OnPause -> pause()
            HomeAction.OnResume -> resume()
            HomeAction.OnStop -> stop()
        }
    }

    private fun start() {
        viewModelScope.launch { startTrackingUseCase() }
        _uiState.update {
            it.copy(
                isStarted = true,
                isPaused = false,
            )
        }
    }

    private fun pause() {
        viewModelScope.launch { pauseTrackingUseCase() }
        _uiState.update {
            it.copy(
                isStarted = false,
                isPaused = true,
            )
        }
    }

    private fun resume() {
        viewModelScope.launch { resumeTrackingUseCase() }
        _uiState.update {
            it.copy(
                isStarted = true,
                isPaused = false,
            )
        }
    }

    private fun stop() {
        val currentState = uiState.value

        _uiState.update {
            it.copy(
                isStarted = false,
                isPaused = false,
            )
        }

        stopTrackingUseCase()

        viewModelScope.launch {
            if (currentState.distance > 0 && currentState.startTime > 0) {
                saveRunningSessionUseCase(
                    RunningSession(
                        startTime = currentState.startTime,
                        endTime = System.currentTimeMillis(),
                        distance = currentState.distance,
                        duration = System.currentTimeMillis() - currentState.startTime,
                        pathPoints = currentState.pathPoints,
                    ),
                )
            }
        }
    }

    private fun updateBatteryStatus(batteryStatus: BatteryState.Status) {
        _uiState.update {
            it.copy(batteryStatus = batteryStatus)
        }
    }

    private fun showSnackbar(message: String) {
        viewModelScope.launch {
            _event.emit(HomeEvent.ShowSnackbar(message))
        }
    }
}