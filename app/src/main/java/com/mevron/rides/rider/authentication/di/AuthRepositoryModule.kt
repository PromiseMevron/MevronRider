package com.mevron.rides.rider.authentication.di

import com.mevron.rides.rider.authentication.data.network.AuthApi
import com.mevron.rides.rider.authentication.data.repository.AuthRepository
import com.mevron.rides.rider.authentication.domain.repository.IAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthRepositoryModule {

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit) = retrofit.create<AuthApi>()

    @Provides
    @Singleton
    fun provideAuthRepository(authApi: AuthApi): IAuthRepository = AuthRepository(authApi)
}