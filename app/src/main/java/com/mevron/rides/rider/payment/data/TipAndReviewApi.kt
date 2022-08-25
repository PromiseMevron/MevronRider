package com.mevron.rides.rider.payment.data

import com.mevron.rides.rider.remote.model.GeneralResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TipAndReviewApi {
    @POST("api/v1/trip/rider/auth/trip/tip")
    suspend fun sendTipAndReview(@Body tipAndReviewRequest: TipAndReviewRequest): Response<GeneralResponse>

    @POST("api/v1/trip/rider/auth/trip/rate")
    suspend fun sendRating(@Body rateDriverRequest: RateDriverRequest): Response<GeneralResponse>

}