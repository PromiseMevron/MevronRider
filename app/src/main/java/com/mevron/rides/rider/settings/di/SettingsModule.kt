package com.mevron.rides.rider.settings.di

import com.mevron.rides.rider.settings.data.SettingsAPI
import com.mevron.rides.rider.settings.data.SettingsRepo
import com.mevron.rides.rider.settings.domain.ISettingsRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): SettingsAPI =
        retrofit.create(SettingsAPI::class.java)


    @Provides
    @Singleton
    fun provideRepository(api: SettingsAPI): ISettingsRepo =
        SettingsRepo(api)
}