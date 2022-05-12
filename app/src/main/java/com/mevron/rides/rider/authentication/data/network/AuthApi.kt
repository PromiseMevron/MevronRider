package com.mevron.rides.rider.authentication.data.network

import com.mevron.rides.rider.authentication.data.models.createaccount.SaveDetailsRequest
import com.mevron.rides.rider.authentication.data.models.profile.GetProfileResponse
import com.mevron.rides.rider.authentication.data.models.registerphone.RegisterBody
import com.mevron.rides.rider.authentication.data.models.registerphone.RegisterPhoneResponse
import com.mevron.rides.rider.authentication.data.models.verifyotp.ValidateOTPRequest
import com.mevron.rides.rider.authentication.data.models.verifyotp.ValidateOTPResponse
import com.mevron.rides.rider.remote.model.GeneralResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/rider/request-otp")
    suspend fun registerPhone(@Body data: RegisterBody): Response<RegisterPhoneResponse>

    @POST("api/v1/rider/validate-otp")
    suspend fun verifyOTP(@Body data: ValidateOTPRequest): Response<ValidateOTPResponse>

    @POST("api/v1/rider/auth/profile")
    suspend fun sendDetail(@Body data: SaveDetailsRequest): Response<GeneralResponse>

    @GET("api/v1/rider/auth/profile")
    suspend fun getProfile(): Response<GetProfileResponse>
}