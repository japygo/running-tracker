package com.japygo.runningtracker.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.japygo.runningtracker.domain.model.RunningSession
import com.japygo.runningtracker.domain.usecase.ObserveTrackingStateUseCase
import com.japygo.runningtracker.domain.usecase.PauseTrackingUseCase
import com.japygo.runningtracker.domain.usecase.ResumeTrackingUseCase
import com.japygo.runningtracker.domain.usecase.SaveRunningSessionUseCase
import com.japygo.runningtracker.domain.usecase.StartTrackingUseCase
import com.japygo.runningtracker.domain.usecase.StopTrackingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeTrackingStateUseCase().collect { state ->
                _uiState.update {
                    uiState.value.copy(
                        distance = state.distance,
                        duration = state.duration,
                        pathPoints = state.pathPoints,
                    )
                }
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
            uiState.value.copy(
                isStarted = true,
                isPaused = false,
            )
        }
    }

    private fun pause() {
        viewModelScope.launch { pauseTrackingUseCase() }
        _uiState.update {
            uiState.value.copy(
                isStarted = false,
                isPaused = true,
            )
        }
    }

    private fun resume() {
        viewModelScope.launch { resumeTrackingUseCase() }
        _uiState.update {
            uiState.value.copy(
                isStarted = true,
                isPaused = false,
            )
        }
    }

    private fun stop() {
        viewModelScope.launch {
            saveRunningSessionUseCase(RunningSession())
            stopTrackingUseCase()
        }
        _uiState.update {
            uiState.value.copy(
                isStarted = false,
                isPaused = false,
            )
        }
    }
}