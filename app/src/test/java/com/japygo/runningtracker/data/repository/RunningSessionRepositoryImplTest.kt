package com.japygo.runningtracker.data.repository

import com.japygo.runningtracker.data.dao.RunningSessionDao
import com.japygo.runningtracker.data.entity.RunningSessionEntity
import com.japygo.runningtracker.data.mapper.toDomain
import com.japygo.runningtracker.data.mapper.toEntity
import com.japygo.runningtracker.domain.model.RunningSession
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RunningSessionRepositoryImplTest {

    private lateinit var repository: RunningSessionRepositoryImpl
    private lateinit var dao: RunningSessionDao

    @Before
    fun setup() {
        dao = mockk()
        repository = RunningSessionRepositoryImpl(dao)
        
        mockkStatic("com.japygo.runningtracker.data.mapper.RunningSessionMapperKt")
    }

    @Test
    fun `insert successfully saves mapped entity`() = runTest {
        val session = RunningSession(
            startTime = 1000L,
            endTime = 2000L,
            distance = 100.0,
            duration = 1000L,
            pathPoints = emptyList(),
        )
        val entity = RunningSessionEntity(
            id = 1,
            startTime = 1000L,
            endTime = 2000L,
            distance = 100.0,
            duration = 1000L,
            pathPointsJson = "[]",
        )

        every { session.toEntity() } returns entity
        coEvery { dao.insert(entity) } returns Unit

        repository.insert(session)

        coVerify(exactly = 1) { dao.insert(entity) }
    }

    @Test
    fun `insert handles domain to entity mapping errors`() = runTest {
        val session = RunningSession(
            startTime = 1000L,
            endTime = 2000L,
            distance = 100.0,
            duration = 1000L,
            pathPoints = emptyList(),
        )
        val exception = RuntimeException("Mapping failed")

        every { session.toEntity() } throws exception

        assertFailsWith<RuntimeException> {
            repository.insert(session)
        }
    }

    @Test
    fun `insert propagates DAO exceptions`() = runTest {
        val session = RunningSession(
            startTime = 1000L,
            endTime = 2000L,
            distance = 100.0,
            duration = 1000L,
            pathPoints = emptyList(),
        )
        val entity = RunningSessionEntity(
            startTime = 1000L,
            endTime = 2000L,
            distance = 100.0,
            duration = 1000L,
            pathPointsJson = "[]",
        )
        val exception = RuntimeException("DAO insert failed")

        every { session.toEntity() } returns entity
        coEvery { dao.insert(entity) } throws exception

        assertFailsWith<RuntimeException> {
            repository.insert(session)
        }
    }

    @Test
    fun `findAll returns mapped list of sessions`() = runTest {
        val entities = listOf(
            RunningSessionEntity(1, 1000L, 2000L, 100.0, 1000L, "[]"),
            RunningSessionEntity(2, 3000L, 4000L, 200.0, 1000L, "[]"),
        )
        val sessions = listOf(
            RunningSession(1000L, 2000L, 100.0, 1000L, emptyList()),
            RunningSession(3000L, 4000L, 200.0, 1000L, emptyList()),
        )

        coEvery { dao.findAll() } returns entities
        entities.forEachIndexed { index, entity ->
            every { entity.toDomain() } returns sessions[index]
        }

        val result = repository.findAll()

        assertEquals(2, result.size)
        coVerify(exactly = 1) { dao.findAll() }
    }

    @Test
    fun `findAll returns empty list when DAO is empty`() = runTest {
        coEvery { dao.findAll() } returns emptyList()

        val result = repository.findAll()

        assertTrue(result.isEmpty())
        coVerify(exactly = 1) { dao.findAll() }
    }

    @Test
    fun `findAll propagates DAO exceptions`() = runTest {
        val exception = RuntimeException("DAO findAll failed")
        coEvery { dao.findAll() } throws exception

        assertFailsWith<RuntimeException> {
            repository.findAll()
        }
    }

    @Test
    fun `findAll handles corrupted entity data mapping`() = runTest {
        val entity = RunningSessionEntity(1, 1000L, 2000L, 100.0, 1000L, "invalid_json")
        val exception = RuntimeException("Mapping failed")

        coEvery { dao.findAll() } returns listOf(entity)
        every { entity.toDomain() } throws exception

        assertFailsWith<RuntimeException> {
            repository.findAll()
        }
    }

    @Test
    fun `findAll handles large dataset performance`() = runTest {
        val entities = (1..1000).map {
            RunningSessionEntity(it.toLong(), 1000L, 2000L, 100.0, 1000L, "[]")
        }
        val sessions = (1..1000).map {
            RunningSession(1000L, 2000L, 100.0, 1000L, emptyList())
        }

        coEvery { dao.findAll() } returns entities
        entities.forEachIndexed { index, entity ->
            every { entity.toDomain() } returns sessions[index]
        }

        val result = repository.findAll()

        assertEquals(1000, result.size)
    }

    @Test
    fun `insert handles cancellation`() = runTest {
        val session = RunningSession(
            startTime = 1000L,
            endTime = 2000L,
            distance = 100.0,
            duration = 1000L,
            pathPoints = emptyList(),
        )
        val entity = RunningSessionEntity(
            startTime = 1000L,
            endTime = 2000L,
            distance = 100.0,
            duration = 1000L,
            pathPointsJson = "[]",
        )

        every { session.toEntity() } returns entity
        coEvery { dao.insert(entity) } throws CancellationException()

        assertFailsWith<CancellationException> {
            repository.insert(session)
        }
    }

    @Test
    fun `findAll handles cancellation`() = runTest {
        coEvery { dao.findAll() } throws CancellationException()

        assertFailsWith<CancellationException> {
            repository.findAll()
        }
    }
}