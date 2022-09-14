package com.mevron.rides.rider.home.data

import com.mevron.rides.rider.authentication.data.models.profile.GetProfileResponse
import com.mevron.rides.rider.remote.model.GeneralResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ProfileApi {
    @GET("api/v1/rider/auth/profile")
    suspend fun getProfile(): Response<GetProfileResponse>

    @POST("api/v1/rider/auth/update-device-id")
    fun updateToken(@Body id: DeviceID): Call<Any>

    @POST("api/v1/rider/auth/update-device-id")
    suspend fun postToken(@Body id: DeviceID): Response<Any>
}