package com.mevron.rides.rider.payment.data

import com.mevron.rides.rider.remote.model.GeneralResponse
import retrofit2.Response
import retrofit2.http.POST

interface TipAndReviewApi {

    @POST("/api/v1/rider/auth/trip/tip")
    fun sendTipAndReview(tipAndReviewRequest: TipAndReviewRequest): Response<GeneralResponse>
}