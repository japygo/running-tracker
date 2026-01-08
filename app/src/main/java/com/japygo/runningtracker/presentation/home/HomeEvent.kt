package com.japygo.runningtracker.presentation.home

sealed interface HomeEvent {
    data class ShowSnackbar(val message: String) : HomeEvent
}