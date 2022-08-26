package com.mevron.rides.rider.settings.referal.di

import com.mevron.rides.rider.settings.referal.data.network.ReferralApi
import com.mevron.rides.rider.settings.referal.data.repository.ReferralRepo
import com.mevron.rides.rider.settings.referal.domain.repository.IReferalRepo
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.Module
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ReferralModule {

    @Provides
    @Singleton
    fun provideReferralApi(retrofit: Retrofit): ReferralApi =
        retrofit.create(ReferralApi::class.java)

    @Provides
    @Singleton
    fun provideReferralApiRepository(api: ReferralApi): IReferalRepo =
        ReferralRepo(api)
}