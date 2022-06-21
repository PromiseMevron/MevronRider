package com.mevron.rides.rider.home.data

import com.mevron.rides.rider.authentication.data.models.profile.GetProfileResponse
import retrofit2.Response
import retrofit2.http.GET

interface ProfileApi {
    @GET("api/v1/rider/auth/profile")
    suspend fun getProfile(): Response<GetProfileResponse>
}