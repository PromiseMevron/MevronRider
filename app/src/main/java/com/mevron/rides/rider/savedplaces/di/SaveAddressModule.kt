package com.mevron.rides.rider.savedplaces.di

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
object SaveAddressModule {

    @Provides
    @Singleton
    fun provideSaveAddressApi(retrofit: Retrofit): AddressAPI =
        retrofit.create(AddressAPI::class.java)

    @Provides
    @Singleton
    fun provideSaveAddressRepository(api: AddressAPI): IAddressRepository =
        AddressRepository(api)
}