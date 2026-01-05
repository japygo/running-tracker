package com.japygo.runningtracker.core.di

import android.content.Context
import androidx.room.Room
import com.japygo.runningtracker.data.dao.RunningSessionDao
import com.japygo.runningtracker.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "running-tracker.db",
        ).build()
    }

    @Provides
    fun provideRunningSessionDao(
        database: AppDatabase,
    ): RunningSessionDao {
        return database.runningSessionDao()
    }
}