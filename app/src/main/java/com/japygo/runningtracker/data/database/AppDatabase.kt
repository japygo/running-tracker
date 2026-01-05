package com.japygo.runningtracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.japygo.runningtracker.data.dao.RunningSessionDao
import com.japygo.runningtracker.data.entity.RunningSessionEntity

@Database(entities = [RunningSessionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun runningSessionDao(): RunningSessionDao
}
