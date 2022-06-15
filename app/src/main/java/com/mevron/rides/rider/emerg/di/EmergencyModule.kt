package com.mevron.rides.rider.emerg.di

import com.mevron.rides.rider.emerg.data.network.EmergencyAPI
import com.mevron.rides.rider.emerg.data.repository.EmergencyRepository
import com.mevron.rides.rider.emerg.domain.repository.IEmergencyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EmergencyModule {

    @Provides
    @Singleton
    fun provideEmergencyApi(retrofit: Retrofit): EmergencyAPI =
        retrofit.create(EmergencyAPI::class.java)

    @Provides
    @Singleton
    fun provideEmergencyRepository(api: EmergencyAPI): IEmergencyRepository =
        EmergencyRepository(api)
}