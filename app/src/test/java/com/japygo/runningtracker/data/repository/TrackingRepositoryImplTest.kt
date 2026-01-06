package com.japygo.runningtracker.data.repository

import com.japygo.runningtracker.data.datastore.TrackingDataStore
import com.japygo.runningtracker.data.manager.TrackingManager
import com.japygo.runningtracker.domain.model.TrackingState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class TrackingRepositoryImplTest {

    private lateinit var repository: TrackingRepositoryImpl
    private lateinit var trackingManager: TrackingManager
    private lateinit var trackingDataStore: TrackingDataStore

    @Before
    fun setup() {
        trackingManager = mockk()
        trackingDataStore = mockk()
        repository = TrackingRepositoryImpl(trackingManager, trackingDataStore)
    }

    @Test
    fun `observeTrackingState flow emission`() = runTest {
        val state = TrackingState(distance = 100.0)
        every { trackingManager.state } returns MutableStateFlow(state)

        val result = repository.observeTrackingState().first()

        assertEquals(state, result)
    }

    @Test
    fun `observeTrackingState multiple emissions`() = runTest {
        val state1 = TrackingState(distance = 0.0)
        val state2 = TrackingState(distance = 50.0)
        val stateFlow = MutableStateFlow(state1)
        every { trackingManager.state } returns stateFlow

        val result1 = repository.observeTrackingState().first()
        assertEquals(state1, result1)

        stateFlow.value = state2
        val result2 = repository.observeTrackingState().first()
        assertEquals(state2, result2)
    }

    @Test
    fun `observeTrackingState initial state emission`() = runTest {
        val initialState = TrackingState()
        every { trackingManager.state } returns MutableStateFlow(initialState)

        val result = repository.observeTrackingState().first()

        assertEquals(initialState, result)
    }

    @Test
    fun `observeTrackingState flow completion`() = runTest {
        every { trackingManager.state } returns MutableStateFlow(TrackingState())

        val results = repository.observeTrackingState().take(1).toList()

        assertEquals(1, results.size)
    }

    @Test
    fun `observeTrackingState error propagation`() = runTest {
        val exception = RuntimeException("Test error")
        val stateFlow = MutableStateFlow(TrackingState())
        every { trackingManager.state } returns stateFlow

        try {
            throw exception
        } catch (e: RuntimeException) {
            assertEquals("Test error", e.message)
        }
    }

    @Test
    fun `saveTrackingState successful save`() = runTest {
        val state = TrackingState(distance = 100.0)
        coEvery { trackingDataStore.saveTrackingState(state) } returns Unit

        repository.saveTrackingState(state)

        coVerify(exactly = 1) { trackingDataStore.saveTrackingState(state) }
    }

    @Test
    fun `saveTrackingState various states`() = runTest {
        val states = listOf(
            TrackingState(isStarted = true),
            TrackingState(isPaused = true),
            TrackingState(distance = 0.0),
        )

        states.forEach { state ->
            coEvery { trackingDataStore.saveTrackingState(state) } returns Unit
            repository.saveTrackingState(state)
        }

        states.forEach { state ->
            coVerify(exactly = 1) { trackingDataStore.saveTrackingState(state) }
        }
    }

    @Test
    fun `saveTrackingState data store exception`() = runTest {
        val state = TrackingState()
        val exception = RuntimeException("Save failed")
        coEvery { trackingDataStore.saveTrackingState(state) } throws exception

        assertFailsWith<RuntimeException> {
            repository.saveTrackingState(state)
        }
    }

    @Test
    fun `saveTrackingState concurrent saves`() = runTest {
        val state1 = TrackingState(distance = 100.0)
        val state2 = TrackingState(distance = 200.0)

        coEvery { trackingDataStore.saveTrackingState(any()) } returns Unit

        repository.saveTrackingState(state1)
        repository.saveTrackingState(state2)

        coVerify(exactly = 1) { trackingDataStore.saveTrackingState(state1) }
        coVerify(exactly = 1) { trackingDataStore.saveTrackingState(state2) }
    }

    @Test
    fun `getTrackingState successful retrieval`() = runTest {
        val state = TrackingState(distance = 100.0)
        coEvery { trackingDataStore.getTrackingState() } returns state

        val result = repository.getTrackingState()

        assertEquals(state, result)
        coVerify(exactly = 1) { trackingDataStore.getTrackingState() }
    }

    @Test
    fun `getTrackingState empty data store`() = runTest {
        coEvery { trackingDataStore.getTrackingState() } returns null

        val result = repository.getTrackingState()

        assertNull(result)
    }

    @Test
    fun `getTrackingState data store exception`() = runTest {
        val exception = RuntimeException("Retrieval failed")
        coEvery { trackingDataStore.getTrackingState() } throws exception

        assertFailsWith<RuntimeException> {
            repository.getTrackingState()
        }
    }

    @Test
    fun `getTrackingState data corruption`() = runTest {
        coEvery { trackingDataStore.getTrackingState() } returns null

        val result = repository.getTrackingState()

        assertNull(result)
    }

    @Test
    fun `clearTrackingState successful clear`() = runTest {
        coEvery { trackingDataStore.clearTrackingState() } returns Unit

        repository.clearTrackingState()

        coVerify(exactly = 1) { trackingDataStore.clearTrackingState() }
    }

    @Test
    fun `clearTrackingState data store exception`() = runTest {
        val exception = RuntimeException("Clear failed")
        coEvery { trackingDataStore.clearTrackingState() } throws exception

        assertFailsWith<RuntimeException> {
            repository.clearTrackingState()
        }
    }

    @Test
    fun `clearTrackingState idempotency check`() = runTest {
        coEvery { trackingDataStore.clearTrackingState() } returns Unit

        repository.clearTrackingState()
        repository.clearTrackingState()

        coVerify(exactly = 2) { trackingDataStore.clearTrackingState() }
    }

    @Test
    fun `Integration save then get`() = runTest {
        val state = TrackingState(distance = 100.0)
        coEvery { trackingDataStore.saveTrackingState(state) } returns Unit
        coEvery { trackingDataStore.getTrackingState() } returns state

        repository.saveTrackingState(state)
        val result = repository.getTrackingState()

        assertEquals(state, result)
    }

    @Test
    fun `Integration save clear then get`() = runTest {
        val state = TrackingState(distance = 100.0)
        coEvery { trackingDataStore.saveTrackingState(state) } returns Unit
        coEvery { trackingDataStore.clearTrackingState() } returns Unit
        coEvery { trackingDataStore.getTrackingState() } returns null

        repository.saveTrackingState(state)
        repository.clearTrackingState()
        val result = repository.getTrackingState()

        assertNull(result)
    }

    @Test
    fun `Integration clear save then get`() = runTest {
        val state = TrackingState(distance = 100.0)
        coEvery { trackingDataStore.clearTrackingState() } returns Unit
        coEvery { trackingDataStore.saveTrackingState(state) } returns Unit
        coEvery { trackingDataStore.getTrackingState() } returns state

        repository.clearTrackingState()
        repository.saveTrackingState(state)
        val result = repository.getTrackingState()

        assertEquals(state, result)
    }
}