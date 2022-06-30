package com.mevron.rides.rider.payment.data

import com.mevron.rides.rider.remote.model.GeneralResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Path

interface PaymentApi {

    @DELETE("api/v1/rider/auth/payment-method/{uiid}/remove")
    suspend fun deleteCard(@Path("uiid") identifier: String): Response<GeneralResponse>
}