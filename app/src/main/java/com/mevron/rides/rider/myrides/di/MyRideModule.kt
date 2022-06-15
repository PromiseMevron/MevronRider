package com.mevron.rides.rider.myrides.di

import com.mevron.rides.rider.myrides.data.network.MyRideAPI
import com.mevron.rides.rider.myrides.data.repository.MyRideRepository
import com.mevron.rides.rider.myrides.domain.repository.IMyRideRepo
import com.mevron.rides.rider.savedplaces.data.network.AddressAPI
import com.mevron.rides.rider.savedplaces.data.repository.AddressRepository
import com.mevron.rides.rider.savedplaces.domain.repository.IAddressRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MyRideModule {

    @Provides
    @Singleton
    fun providemyRideApi(retrofit: Retrofit): MyRideAPI =
        retrofit.create(MyRideAPI::class.java)

    @Provides
    @Singleton
    fun provideSaveAddressRepository(api: MyRideAPI): IMyRideRepo =
        MyRideRepository(api)
}