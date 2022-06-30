package com.mevron.rides.rider.socket.di

import com.mevron.rides.rider.data.TripStateRepository
import com.mevron.rides.rider.domain.ITripStateRepository
import com.mevron.rides.rider.sharedprefrence.domain.repository.IPreferenceRepository
import com.mevron.rides.rider.socket.data.SocketManager
import com.mevron.rides.rider.socket.domain.ISocketManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SocketModule {

    @Provides
    @Singleton
    fun provideTripStateRepository(): ITripStateRepository = TripStateRepository()

    @Provides
    @Singleton
    fun provideSocketManager(
        preferenceRepository: IPreferenceRepository,
        tripRepository: ITripStateRepository
    ): ISocketManager = SocketManager(
        preferenceRepository,
        tripRepository
    )
}