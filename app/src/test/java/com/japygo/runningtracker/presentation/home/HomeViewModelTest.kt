package com.japygo.runningtracker.presentation.home

import com.japygo.runningtracker.domain.model.RunningSession
import com.japygo.runningtracker.domain.model.TrackingState
import com.japygo.runningtracker.domain.usecase.ObserveTrackingStateUseCase
import com.japygo.runningtracker.domain.usecase.PauseTrackingUseCase
import com.japygo.runningtracker.domain.usecase.ResumeTrackingUseCase
import com.japygo.runningtracker.domain.usecase.SaveRunningSessionUseCase
import com.japygo.runningtracker.domain.usecase.StartTrackingUseCase
import com.japygo.runningtracker.domain.usecase.StopTrackingUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var startTrackingUseCase: StartTrackingUseCase
    private lateinit var pauseTrackingUseCase: PauseTrackingUseCase
    private lateinit var resumeTrackingUseCase: ResumeTrackingUseCase
    private lateinit var stopTrackingUseCase: StopTrackingUseCase
    private lateinit var observeTrackingStateUseCase: ObserveTrackingStateUseCase
    private lateinit var saveRunningSessionUseCase: SaveRunningSessionUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        startTrackingUseCase = mockk(relaxed = true)
        pauseTrackingUseCase = mockk(relaxed = true)
        resumeTrackingUseCase = mockk(relaxed = true)
        stopTrackingUseCase = mockk(relaxed = true)
        observeTrackingStateUseCase = mockk()
        saveRunningSessionUseCase = mockk(relaxed = true)

        every { observeTrackingStateUseCase() } returns flowOf(TrackingState())

        viewModel = HomeViewModel(
            startTrackingUseCase,
            pauseTrackingUseCase,
            resumeTrackingUseCase,
            stopTrackingUseCase,
            observeTrackingStateUseCase,
            saveRunningSessionUseCase,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Initial UI state verification`() = runTest {
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isStarted)
        assertFalse(state.isPaused)
        assertEquals(0.0, state.distance)
        assertEquals(0L, state.duration)
        assertEquals(0L, state.startTime)
        assertTrue(state.pathPoints.isEmpty())
        assertEquals(null, state.currentLocation)
        assertTrue(state.isGpsAvailable)
    }

    @Test
    fun `Tracking state collection updates UI`() = runTest {
        val trackingState = TrackingState(
            distance = 100.0,
            duration = 5000L,
            pathPoints = listOf(Pair(37.5, 127.0)),
            startTime = 1000L,
            currentLocation = Pair(37.5, 127.0),
            isGpsAvailable = false,
        )

        every { observeTrackingStateUseCase() } returns flowOf(trackingState)

        viewModel = HomeViewModel(
            startTrackingUseCase,
            pauseTrackingUseCase,
            resumeTrackingUseCase,
            stopTrackingUseCase,
            observeTrackingStateUseCase,
            saveRunningSessionUseCase,
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(100.0, state.distance)
        assertEquals(5000L, state.duration)
        assertEquals(1, state.pathPoints.size)
        assertEquals(1000L, state.startTime)
        assertEquals(Pair(37.5, 127.0), state.currentLocation)
        assertFalse(state.isGpsAvailable)
    }

    @Test
    fun `Tracking state collection maintains start pause flags`() = runTest {
        val trackingState = TrackingState(distance = 100.0)
        every { observeTrackingStateUseCase() } returns flowOf(trackingState)

        viewModel = HomeViewModel(
            startTrackingUseCase,
            pauseTrackingUseCase,
            resumeTrackingUseCase,
            stopTrackingUseCase,
            observeTrackingStateUseCase,
            saveRunningSessionUseCase,
        )

        viewModel.onAction(HomeAction.OnStart)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isStarted)
        assertFalse(state.isPaused)
    }

    @Test
    fun `Action OnStart triggers use case and updates state`() = runTest {
        coEvery { startTrackingUseCase() } returns Unit

        viewModel.onAction(HomeAction.OnStart)
        advanceUntilIdle()

        coVerify(exactly = 1) { startTrackingUseCase() }
        val state = viewModel.uiState.value
        assertTrue(state.isStarted)
        assertFalse(state.isPaused)
    }

    @Test
    fun `Action OnPause triggers use case and updates state`() = runTest {
        coEvery { pauseTrackingUseCase() } returns Unit

        viewModel.onAction(HomeAction.OnPause)
        advanceUntilIdle()

        coVerify(exactly = 1) { pauseTrackingUseCase() }
        val state = viewModel.uiState.value
        assertFalse(state.isStarted)
        assertTrue(state.isPaused)
    }

    @Test
    fun `Action OnResume triggers use case and updates state`() = runTest {
        coEvery { resumeTrackingUseCase() } returns Unit

        viewModel.onAction(HomeAction.OnResume)
        advanceUntilIdle()

        coVerify(exactly = 1) { resumeTrackingUseCase() }
        val state = viewModel.uiState.value
        assertTrue(state.isStarted)
        assertFalse(state.isPaused)
    }

    @Test
    fun `Action OnStop triggers use case and resets state`() = runTest {
        coEvery { stopTrackingUseCase() } returns Unit

        viewModel.onAction(HomeAction.OnStop)
        advanceUntilIdle()

        coVerify(exactly = 1) { stopTrackingUseCase() }
        val state = viewModel.uiState.value
        assertFalse(state.isStarted)
        assertFalse(state.isPaused)
    }

    @Test
    fun `Save session on Stop with valid data`() = runTest {
        val trackingState = TrackingState(
            distance = 100.0,
            startTime = 1000L,
        )
        every { observeTrackingStateUseCase() } returns flowOf(trackingState)

        viewModel = HomeViewModel(
            startTrackingUseCase,
            pauseTrackingUseCase,
            resumeTrackingUseCase,
            stopTrackingUseCase,
            observeTrackingStateUseCase,
            saveRunningSessionUseCase,
        )

        advanceUntilIdle()

        coEvery { stopTrackingUseCase() } returns Unit
        coEvery { saveRunningSessionUseCase(any()) } returns Unit

        viewModel.onAction(HomeAction.OnStop)
        advanceUntilIdle()

        coVerify(exactly = 1) { saveRunningSessionUseCase(any()) }
    }

    @Test
    fun `Skip save session on Stop with zero distance`() = runTest {
        val trackingState = TrackingState(
            distance = 0.0,
            startTime = 1000L,
        )
        every { observeTrackingStateUseCase() } returns flowOf(trackingState)

        viewModel = HomeViewModel(
            startTrackingUseCase,
            pauseTrackingUseCase,
            resumeTrackingUseCase,
            stopTrackingUseCase,
            observeTrackingStateUseCase,
            saveRunningSessionUseCase,
        )

        advanceUntilIdle()

        coEvery { stopTrackingUseCase() } returns Unit

        viewModel.onAction(HomeAction.OnStop)
        advanceUntilIdle()

        coVerify(exactly = 0) { saveRunningSessionUseCase(any()) }
    }

    @Test
    fun `Skip save session on Stop with zero start time`() = runTest {
        val trackingState = TrackingState(
            distance = 100.0,
            startTime = 0L,
        )
        every { observeTrackingStateUseCase() } returns flowOf(trackingState)

        viewModel = HomeViewModel(
            startTrackingUseCase,
            pauseTrackingUseCase,
            resumeTrackingUseCase,
            stopTrackingUseCase,
            observeTrackingStateUseCase,
            saveRunningSessionUseCase,
        )

        advanceUntilIdle()

        coEvery { stopTrackingUseCase() } returns Unit

        viewModel.onAction(HomeAction.OnStop)
        advanceUntilIdle()

        coVerify(exactly = 0) { saveRunningSessionUseCase(any()) }
    }

    @Test
    fun `Skip save session on Stop with zero distance and time`() = runTest {
        coEvery { stopTrackingUseCase() } returns Unit

        viewModel.onAction(HomeAction.OnStop)
        advanceUntilIdle()

        coVerify(exactly = 0) { saveRunningSessionUseCase(any()) }
    }

    @Test
    fun `Save session calculates duration correctly`() = runTest {
        val startTime = 1000L
        val trackingState = TrackingState(
            distance = 100.0,
            startTime = startTime,
        )
        every { observeTrackingStateUseCase() } returns flowOf(trackingState)

        viewModel = HomeViewModel(
            startTrackingUseCase,
            pauseTrackingUseCase,
            resumeTrackingUseCase,
            stopTrackingUseCase,
            observeTrackingStateUseCase,
            saveRunningSessionUseCase,
        )

        advanceUntilIdle()

        coEvery { stopTrackingUseCase() } returns Unit

        val sessionSlot = slot<RunningSession>()
        coEvery { saveRunningSessionUseCase(capture(sessionSlot)) } returns Unit

        viewModel.onAction(HomeAction.OnStop)
        advanceUntilIdle()

        val capturedSession = sessionSlot.captured
        assertTrue(capturedSession.duration > 0)
        assertTrue(capturedSession.endTime > capturedSession.startTime)
    }

    @Test
    fun `Coroutine scope handling for use cases`() = runTest {
        coEvery { startTrackingUseCase() } returns Unit

        viewModel.onAction(HomeAction.OnStart)

        advanceUntilIdle()

        coVerify(exactly = 1) { startTrackingUseCase() }
    }

    @Test
    fun `State consistency during rapid Action toggling`() = runTest {
        coEvery { startTrackingUseCase() } returns Unit
        coEvery { pauseTrackingUseCase() } returns Unit
        coEvery { resumeTrackingUseCase() } returns Unit
        coEvery { stopTrackingUseCase() } returns Unit

        viewModel.onAction(HomeAction.OnStart)
        viewModel.onAction(HomeAction.OnPause)
        viewModel.onAction(HomeAction.OnResume)
        viewModel.onAction(HomeAction.OnStop)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isStarted)
        assertFalse(state.isPaused)
    }
}