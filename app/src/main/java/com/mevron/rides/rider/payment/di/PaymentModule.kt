package com.mevron.rides.rider.payment.di

import com.mevron.rides.rider.payment.data.PaymentOptionsApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
object PaymentModule {

    @Provides
    @Singleton
    fun providePaymentOptionApi(retrofit: Retrofit): PaymentOptionsApi
        = retrofit.create(PaymentOptionsApi::class.java)


}