package com.japygo.runningtracker.presentation.home

sealed interface HomeAction {
    data object OnStart : HomeAction
    data object OnPause : HomeAction
    data object OnResume : HomeAction
    data object OnStop : HomeAction
    data class UpdateLocationPermission(val hasPermission: Boolean) : HomeAction
}