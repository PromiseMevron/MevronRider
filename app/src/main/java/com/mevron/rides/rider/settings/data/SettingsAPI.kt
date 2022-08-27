package com.mevron.rides.rider.settings.data

import com.mevron.rides.rider.remote.model.GeneralResponse
import retrofit2.Response
import retrofit2.http.POST

interface SettingsAPI {
    @POST("api/v1/rider/auth/sign-out")
    suspend fun signOut(): Response<GeneralResponse>
}