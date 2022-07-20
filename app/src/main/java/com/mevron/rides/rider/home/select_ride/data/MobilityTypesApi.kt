package com.mevron.rides.rider.home.select_ride.data

import com.mevron.rides.rider.home.select_ride.domain.MobilityTypesRequest
import com.mevron.rides.rider.home.select_ride.model.GetCarsCategory2
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MobilityTypesApi {

    @POST("api/v1/trip/rider/auth/carCategories")
    suspend fun getMobilityTypes(@Body data: MobilityTypesRequest): Response<GetCarsCategory2>
}