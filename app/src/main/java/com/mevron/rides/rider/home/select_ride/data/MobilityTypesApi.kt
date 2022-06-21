package com.mevron.rides.rider.home.select_ride.data

import com.mevron.rides.rider.home.model.cars.GetCarRequests
import com.mevron.rides.rider.home.select_ride.model.GetCarsCategory2
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MobilityTypesApi {

    @POST("api/v1/rider/auth/carCategories")
    suspend fun getMobilityTypes(@Body data: GetCarRequests): Response<GetCarsCategory2>
}