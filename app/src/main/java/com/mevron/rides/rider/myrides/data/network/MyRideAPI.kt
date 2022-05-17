package com.mevron.rides.rider.myrides.data.network

import com.mevron.rides.rider.remote.model.GeneralResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MyRideAPI {
    @GET("api/v1/rider/auth/trips")
    suspend fun getAllTrips(): Response<GeneralResponse>

    @GET("api/v1/rider/auth/trips/{uiid}")
    suspend fun getTripDetail(@Path("uiid") id: String): Response<GeneralResponse>
}