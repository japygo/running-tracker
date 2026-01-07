package com.japygo.runningtracker.presentation.home

import android.content.Intent
import android.content.IntentFilter
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.japygo.runningtracker.data.receiver.BatteryStateReceiver

@Composable
fun HomeRoot(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentView = LocalView.current
    val context = LocalContext.current
    val batteryStateReceiver = remember { BatteryStateReceiver() }

    DisposableEffect(uiState.isStarted) {
        currentView.keepScreenOn = uiState.isStarted
        onDispose {
            currentView.keepScreenOn = false
        }
    }

    LifecycleStartEffect(true) {
        ContextCompat.registerReceiver(
            context,
            batteryStateReceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        onStopOrDispose { context.unregisterReceiver(batteryStateReceiver) }
    }

    LaunchedEffect(viewModel.event) {
        viewModel.event.collect { event ->
            when (event) {
                is HomeEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        HomeScreen(
            modifier = modifier,
            uiState = uiState,
            onAction = viewModel::onAction,
        )
    }
}