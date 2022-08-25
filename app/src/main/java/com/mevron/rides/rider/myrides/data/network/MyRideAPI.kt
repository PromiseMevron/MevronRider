package com.mevron.rides.rider.myrides.data.network

import com.mevron.rides.rider.myrides.data.model.AllTripsResponse
import com.mevron.rides.rider.myrides.data.model.aTripModel.TripDetailResponse
import com.mevron.rides.rider.remote.model.GeneralResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MyRideAPI {
    @GET("api/v1/trip/rider/auth/trips")
    suspend fun getAllTrips(): Response<AllTripsResponse>

    @GET("api/v1/trip/rider/auth/trip/view/{uuid}")
    suspend fun getTripDetail(@Path("uuid") id: String): Response<TripDetailResponse>
}