package com.japygo.runningtracker.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_session")
data class RunningSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "start_time") val startTime: Long = 0,
    @ColumnInfo(name = "end_time") val endTime: Long = 0,
    @ColumnInfo(name = "distance") val distance: Double = 0.0,
    @ColumnInfo(name = "duration") val duration: Long = 0,
)