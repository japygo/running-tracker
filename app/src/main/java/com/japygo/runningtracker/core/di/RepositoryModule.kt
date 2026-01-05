package com.japygo.runningtracker.core.di

import com.japygo.runningtracker.data.repository.RunningSessionRepositoryImpl
import com.japygo.runningtracker.data.repository.TrackingRepositoryImpl
import com.japygo.runningtracker.domain.repository.RunningSessionRepository
import com.japygo.runningtracker.domain.repository.TrackingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindTrackingRepository(
        impl: TrackingRepositoryImpl,
    ): TrackingRepository

    @Binds
    @Singleton
    fun provideRunningSessionRepository(
        impl: RunningSessionRepositoryImpl,
    ): RunningSessionRepository
}