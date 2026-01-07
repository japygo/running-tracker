package com.japygo.runningtracker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.japygo.runningtracker.data.entity.RunningSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RunningSessionDao {

    @Insert
    suspend fun insert(runningSessionEntity: RunningSessionEntity)

    @Query("SELECT * FROM running_session")
    fun findAll(): Flow<List<RunningSessionEntity>>
}