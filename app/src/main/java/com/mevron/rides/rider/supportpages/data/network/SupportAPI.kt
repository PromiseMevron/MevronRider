package com.mevron.rides.rider.supportpages.data.network

import com.mevron.rides.rider.supportpages.data.model.NotificationResponse
import com.mevron.rides.rider.supportpages.data.model.PromoResponse
import retrofit2.Response
import retrofit2.http.GET

interface SupportAPI {

    @GET("/api/v1/rider/auth/promo-code")
    suspend fun getPromo(): Response<PromoResponse>

    @GET("/api/v1/rider/auth/notifications?page=1")
    suspend fun getNotifications(): Response<NotificationResponse>
}