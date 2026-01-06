package com.japygo.runningtracker.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState = HomeUiState(),
    onAction: (HomeAction) -> Unit = {},
) {
    val cameraPositionState = rememberCameraPositionState()
    var hasMoved by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.currentLocation) {
        if (!hasMoved) {
            uiState.currentLocation?.let {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(
                        LatLng(it.first, it.second),
                        15f,
                    ),
                    durationMs = 1000,
                )
                hasMoved = true
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = true,
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true,
            ),
        ) {
            Polyline(
                points = uiState.pathPoints.map { LatLng(it.first, it.second) },
                width = 10f,
                visible = uiState.pathPoints.isNotEmpty(),
            )
        }

        val icon = if (uiState.isStarted) {
            Icons.Filled.Pause
        } else if (uiState.isPaused) {
            Icons.Filled.PlayArrow
        } else {
            Icons.Filled.PlayArrow
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FloatingActionButton(
                onClick = {
                    if (uiState.isStarted) {
                        onAction(HomeAction.OnPause)
                    } else if (uiState.isPaused) {
                        onAction(HomeAction.OnResume)
                    } else {
                        onAction(HomeAction.OnStart)
                    }
                },
            ) {
                Icon(icon, "Start Button")
            }
            FloatingActionButton(
                onClick = {
                    onAction(HomeAction.OnStop)
                },
            ) {
                Icon(Icons.Filled.Stop, "Stop Button")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}