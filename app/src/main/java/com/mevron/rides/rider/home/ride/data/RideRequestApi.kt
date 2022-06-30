package com.mevron.rides.rider.home.ride.data

import com.mevron.rides.rider.home.ride.model.ConfirmRideResponse
import com.mevron.rides.rider.remote.model.RideRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RideRequestApi {
    @POST("api/v1/rider/auth/requestRide")
    suspend fun makeARideRequest(@Body data: RideRequest): Response<ConfirmRideResponse>
}