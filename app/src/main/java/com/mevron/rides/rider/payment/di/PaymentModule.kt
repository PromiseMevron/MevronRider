package com.mevron.rides.rider.payment.di

import com.mevron.rides.rider.payment.data.PaymentOptionsApi
import com.mevron.rides.rider.payment.data.PaymentOptionsRepository
import com.mevron.rides.rider.payment.data.RatingApi
import com.mevron.rides.rider.payment.data.RatingRepository
import com.mevron.rides.rider.payment.data.TipAndReviewApi
import com.mevron.rides.rider.payment.data.TipAndReviewRepository
import com.mevron.rides.rider.payment.domain.IPaymentOptionsRepository
import com.mevron.rides.rider.payment.domain.IRatingRepository
import com.mevron.rides.rider.payment.domain.ITipAndReviewRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object PaymentModule {

    @Provides
    @Singleton
    fun providePaymentOptionApi(retrofit: Retrofit): PaymentOptionsApi =
        retrofit.create(PaymentOptionsApi::class.java)

    @Provides
    @Singleton
    fun provideTipAndReviewApi(retrofit: Retrofit): TipAndReviewApi =
        retrofit.create(TipAndReviewApi::class.java)

    @Provides
    @Singleton
    fun provideRatingApi(retrofit: Retrofit): RatingApi =
        retrofit.create(RatingApi::class.java)

    @Provides
    @Singleton
    fun providePaymentOptionsRepository(api: PaymentOptionsApi): IPaymentOptionsRepository =
        PaymentOptionsRepository(api)

    @Provides
    @Singleton
    fun provideTipAndReviewRepository(api: TipAndReviewApi): ITipAndReviewRepository =
        TipAndReviewRepository(api)

    @Provides
    @Singleton
    fun provideRatingRepository(api: RatingApi): IRatingRepository =
        RatingRepository(api)
}