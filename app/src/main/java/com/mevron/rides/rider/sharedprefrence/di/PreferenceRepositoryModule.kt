package com.mevron.rides.rider.sharedprefrence.di

import android.content.Context
import com.mevron.rides.rider.sharedprefrence.data.PreferenceRepository
import com.mevron.rides.rider.sharedprefrence.domain.repository.IPreferenceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceRepositoryModule {

    @Provides
    @Singleton
    fun providePreferenceRepository(@ApplicationContext context: Context): IPreferenceRepository =
        PreferenceRepository(context)
}