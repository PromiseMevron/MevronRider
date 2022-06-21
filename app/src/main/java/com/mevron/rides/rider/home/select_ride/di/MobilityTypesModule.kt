package com.mevron.rides.rider.home.select_ride.di

import com.mevron.rides.rider.home.select_ride.data.MobilityTypesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object MobilityTypesModule {

    @Provides
    @Singleton
    fun provideMobilityTypesApi(retrofit: Retrofit): MobilityTypesApi =
        retrofit.create(MobilityTypesApi::class.java)
}