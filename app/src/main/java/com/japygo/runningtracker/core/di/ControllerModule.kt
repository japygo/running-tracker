package com.japygo.runningtracker.core.di

import com.japygo.runningtracker.data.controller.TrackingServiceControllerImpl
import com.japygo.runningtracker.domain.controller.TrackingServiceController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ControllerModule {

    @Binds
    @Singleton
    fun bindTrackingServiceController(
        impl: TrackingServiceControllerImpl,
    ): TrackingServiceController
}