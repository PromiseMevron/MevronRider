package com.mevron.rides.rider.remote

import com.mevron.rides.rider.auth.model.details.SaveDetailsRequest
import com.mevron.rides.rider.auth.model.details.SaveResponse
import com.mevron.rides.rider.auth.model.otp.OTPResponse
import com.mevron.rides.rider.auth.model.otp.ValidateOTPRequest
import com.mevron.rides.rider.auth.model.register.RegisterBody
import com.mevron.rides.rider.auth.model.register.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface MevronAPI {

    @POST("api/v1/rider/request-otp")
    suspend fun registerPhone(@Body data: RegisterBody): Response<RegisterResponse>

    @POST("api/v1/rider/validate-otp")
    suspend fun verifyOTP(@Body data: ValidateOTPRequest): Response<OTPResponse>

    @POST("api/v1/rider/auth/update-profile")
    suspend fun sendDetail(@Body data: SaveDetailsRequest): Response<SaveResponse>

    @GET("api/v1/rider/auth/profile")
    suspend fun getProfile(): Response<SaveResponse>
}