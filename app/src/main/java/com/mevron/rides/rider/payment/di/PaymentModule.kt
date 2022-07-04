package com.mevron.rides.rider.payment.di

import com.mevron.rides.rider.payment.data.PaymentOptionsApi
import com.mevron.rides.rider.payment.data.PaymentOptionsRepository
import com.mevron.rides.rider.payment.domain.IPaymentOptionsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
object PaymentModule {

    @Provides
    @Singleton
    fun providePaymentOptionApi(retrofit: Retrofit): PaymentOptionsApi =
        retrofit.create(PaymentOptionsApi::class.java)

    @Provides
    @Singleton
    fun providePaymentOptionsRepository(api: PaymentOptionsApi): IPaymentOptionsRepository =
        PaymentOptionsRepository(api)
}