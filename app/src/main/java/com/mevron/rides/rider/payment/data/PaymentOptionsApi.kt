package com.mevron.rides.rider.payment.data

import com.mevron.rides.rider.home.model.AddCard
import com.mevron.rides.rider.home.model.GetLinkAmount
import com.mevron.rides.rider.home.model.getCard.GetCardResponse
import com.mevron.rides.rider.remote.model.GeneralResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentOptionsApi {
    @POST("api/v1/rider/auth/payment-method/create")
    suspend fun addCard(@Body data: AddCard): Response<GeneralResponse>

    @GET("api/v1/rider/auth/payment-method")
    suspend fun getCards(): Response<GetCardResponse>

    @DELETE("api/v1/rider/auth/payment-method/{uiid}/remove")
    suspend fun deleteCard(@Path("uiid") identifier: String): Response<GeneralResponse>

    @POST("api/v1/rider/auth/payment-method/payment-link")
    suspend fun getPaymentLink(@Body data: GetLinkAmount): Response<PaymentLink>

    @GET("api/v1/rider/auth/wallet/details")
    suspend fun getWalletDetails(): Response<PaymentDetailsResponse>

    @GET("api/v1/rider/auth/wallet/addFunds")
    suspend fun addFund(data: CashActionData): Response<GeneralResponse>
}