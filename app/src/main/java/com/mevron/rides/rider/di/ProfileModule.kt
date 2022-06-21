package com.mevron.rides.rider.di

import com.mevron.rides.rider.home.data.ProfileApi
import com.mevron.rides.rider.home.data.ProfileRepository
import com.mevron.rides.rider.home.domain.IProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): ProfileApi = retrofit.create(ProfileApi::class.java)

    @Provides
    @Singleton
    fun provideProfileRepository(api: ProfileApi): IProfileRepository
        = ProfileRepository(api)
}