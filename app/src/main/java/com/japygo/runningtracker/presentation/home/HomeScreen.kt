package com.japygo.runningtracker.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = uiState.hasLocationPermission),
            uiSettings = MapUiSettings(myLocationButtonEnabled = uiState.hasLocationPermission),
        ) {
            Polyline(
                points = uiState.pathPoints.map { LatLng(it.first, it.second) },
                width = 10f,
                visible = uiState.pathPoints.isNotEmpty(),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (uiState.runningSessions.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(16.dp),
                        )
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        items = uiState.runningSessions,
                        key = { it.id },
                    ) { session ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .animateItem(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = "🏃 ${String.format("%.2f", session.distance / 1000.0)}km")
                            Text(text = "⏱️ ${session.duration / 1000}초")
                        }
                    }
                }
            }

            if (!uiState.isGpsAvailable && uiState.isStarted) {
                Text(
                    text = "⚠️ GPS 연결이 끊겼습니다",
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .background(
                            Color.Red.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
            ) {
                val icon = if (uiState.isStarted) Icons.Filled.Pause else Icons.Filled.PlayArrow

                FloatingActionButton(
                    onClick = {
                        when {
                            uiState.isStarted -> onAction(HomeAction.OnPause)
                            uiState.isPaused -> onAction(HomeAction.OnResume)
                            else -> onAction(HomeAction.OnStart)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Icon(icon, contentDescription = "Start/Pause")
                }

                FloatingActionButton(
                    onClick = { onAction(HomeAction.OnStop) },
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                ) {
                    Icon(Icons.Filled.Stop, contentDescription = "Stop")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}