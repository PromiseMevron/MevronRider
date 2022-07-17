package com.mevron.rides.rider.home.ride.di

import com.mevron.rides.rider.home.ride.data.RideRequestApi
import com.mevron.rides.rider.home.ride.data.RideRequestRepository
import com.mevron.rides.rider.home.ride.domain.IRideRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object RideRequestModule {

    @Provides
    @Singleton
    fun provideRideRequestApi(retrofit: Retrofit): RideRequestApi =
        retrofit.create(RideRequestApi::class.java)

    @Provides
    @Singleton
    fun provideRideRequestRepository(api: RideRequestApi): IRideRepository =
        RideRequestRepository(api)
}