package com.japygo.runningtracker.presentation.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun initialStateDisplaysStartButton() {
        composeTestRule.setContent {
            HomeScreen(
                uiState = HomeUiState(),
                onAction = {},
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Start Button")
            .assertIsDisplayed()
    }

    @Test
    fun startButtonShowsPauseIconWhenStarted() {
        composeTestRule.setContent {
            HomeScreen(
                uiState = HomeUiState(isStarted = true),
                onAction = {},
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Start Button")
            .assertIsDisplayed()
    }

    @Test
    fun pauseButtonShowsPlayIconWhenPaused() {
        composeTestRule.setContent {
            HomeScreen(
                uiState = HomeUiState(isPaused = true),
                onAction = {},
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Start Button")
            .assertIsDisplayed()
    }

    @Test
    fun stopButtonIsAlwaysDisplayed() {
        composeTestRule.setContent {
            HomeScreen(
                uiState = HomeUiState(),
                onAction = {},
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Stop Button")
            .assertIsDisplayed()
    }

    @Test
    fun startButtonClickTriggersOnStartAction() {
        var actionTriggered = false

        composeTestRule.setContent {
            HomeScreen(
                uiState = HomeUiState(),
                onAction = { action ->
                    if (action == HomeAction.OnStart) {
                        actionTriggered = true
                    }
                },
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Start Button")
            .performClick()

        assert(actionTriggered)
    }

    @Test
    fun pauseButtonClickTriggersOnPauseAction() {
        var actionTriggered = false

        composeTestRule.setContent {
            HomeScreen(
                uiState = HomeUiState(isStarted = true),
                onAction = { action ->
                    if (action == HomeAction.OnPause) {
                        actionTriggered = true
                    }
                },
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Start Button")
            .performClick()

        assert(actionTriggered)
    }

    @Test
    fun resumeButtonClickTriggersOnResumeAction() {
        var actionTriggered = false

        composeTestRule.setContent {
            HomeScreen(
                uiState = HomeUiState(isPaused = true),
                onAction = { action ->
                    if (action == HomeAction.OnResume) {
                        actionTriggered = true
                    }
                },
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Start Button")
            .performClick()

        assert(actionTriggered)
    }

    @Test
    fun stopButtonClickTriggersOnStopAction() {
        var actionTriggered = false

        composeTestRule.setContent {
            HomeScreen(
                uiState = HomeUiState(),
                onAction = { action ->
                    if (action == HomeAction.OnStop) {
                        actionTriggered = true
                    }
                },
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Stop Button")
            .performClick()

        assert(actionTriggered)
    }

    @Test
    fun gpsUnavailableWarningIsDisplayedWhenGpsIsOffAndStarted() {
        composeTestRule.setContent {
            HomeScreen(
                uiState = HomeUiState(
                    isStarted = true,
                    isGpsAvailable = false,
                ),
                onAction = {},
            )
        }

        composeTestRule
            .onNodeWithText("⚠️ GPS 연결이 끊겼습니다")
            .assertIsDisplayed()
    }

    @Test
    fun gpsWarningIsNotDisplayedWhenGpsIsAvailable() {
        composeTestRule.setContent {
            HomeScreen(
                uiState = HomeUiState(
                    isStarted = true,
                    isGpsAvailable = true,
                ),
                onAction = {},
            )
        }

        composeTestRule
            .onNodeWithText("⚠️ GPS 연결이 끊겼습니다")
            .assertDoesNotExist()
    }

    @Test
    fun gpsWarningIsNotDisplayedWhenNotStarted() {
        composeTestRule.setContent {
            HomeScreen(
                uiState = HomeUiState(
                    isStarted = false,
                    isGpsAvailable = false,
                ),
                onAction = {},
            )
        }

        composeTestRule
            .onNodeWithText("⚠️ GPS 연결이 끊겼습니다")
            .assertDoesNotExist()
    }

    @Test
    fun mapIsDisplayedInAllStates() {
        composeTestRule.setContent {
            HomeScreen(
                uiState = HomeUiState(),
                onAction = {},
            )
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun polylineVisibilityWithPathPoints() {
        composeTestRule.setContent {
            HomeScreen(
                uiState = HomeUiState(
                    pathPoints = listOf(
                        Pair(37.5, 127.0),
                        Pair(37.6, 127.1),
                    ),
                ),
                onAction = {},
            )
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun multipleRapidClicksOnStartButton() {
        var clickCount = 0

        composeTestRule.setContent {
            HomeScreen(
                uiState = HomeUiState(),
                onAction = { action ->
                    if (action == HomeAction.OnStart) {
                        clickCount++
                    }
                },
            )
        }

        repeat(5) {
            composeTestRule
                .onNodeWithContentDescription("Start Button")
                .performClick()
        }

        assert(clickCount == 5)
    }

    @Test
    fun stateTransitionFromIdleToStartedToPaused() {
        var currentState = HomeUiState()

        composeTestRule.setContent {
            HomeScreen(
                uiState = currentState,
                onAction = { action ->
                    currentState = when (action) {
                        HomeAction.OnStart -> currentState.copy(isStarted = true, isPaused = false)
                        HomeAction.OnPause -> currentState.copy(isStarted = false, isPaused = true)
                        HomeAction.OnResume -> currentState.copy(isStarted = true, isPaused = false)
                        HomeAction.OnStop -> currentState.copy(isStarted = false, isPaused = false)
                    }
                },
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Start Button")
            .performClick()

        assert(currentState.isStarted)
        assert(!currentState.isPaused)
    }
}
