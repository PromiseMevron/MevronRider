package com.mevron.rides.rider.remote

import com.mevron.rides.rider.auth.model.details.SaveDetailsRequest
import com.mevron.rides.rider.auth.model.details.SaveResponse
import com.mevron.rides.rider.auth.model.otp.OTPResponse
import com.mevron.rides.rider.auth.model.otp.ValidateOTPRequest
import com.mevron.rides.rider.auth.model.register.RegisterBody
import com.mevron.rides.rider.auth.model.register.RegisterResponse
import com.mevron.rides.rider.home.model.AddCard
import com.mevron.rides.rider.home.model.cars.GetCarRequests
import com.mevron.rides.rider.home.model.cars.GetCarsCategory
import com.mevron.rides.rider.home.model.getAddress.GetSavedAddresss
import com.mevron.rides.rider.home.model.getAddress.SaveAddressRequest
import com.mevron.rides.rider.home.model.getAddress.UpdateAddress
import com.mevron.rides.rider.home.model.getCard.GetCardResponse
import com.mevron.rides.rider.home.ride.model.ConfirmRideResponse
import com.mevron.rides.rider.remote.model.GeneralResponse
import com.mevron.rides.rider.remote.model.RideRequest
import retrofit2.Response
import retrofit2.http.*


interface MevronAPI {

    @POST("api/v1/rider/request-otp")
    suspend fun registerPhone(@Body data: RegisterBody): Response<RegisterResponse>

    @POST("api/v1/rider/validate-otp")
    suspend fun verifyOTP(@Body data: ValidateOTPRequest): Response<OTPResponse>

    @POST("api/v1/rider/auth/update-profile")
    suspend fun sendDetail(@Body data: SaveDetailsRequest): Response<SaveResponse>

    @GET("api/v1/rider/auth/profile")
    suspend fun getProfile(): Response<SaveResponse>

    @POST("api/v1/rider/auth/savedPlaces")
    suspend fun saveAddress(@Body data: SaveAddressRequest):Response<GeneralResponse>

    @POST("api/v1/rider/auth/carCategories")
    suspend fun getCars(@Body data: GetCarRequests):Response<GetCarsCategory>

    @GET("api/v1/rider/auth/savedPlaces")
    suspend fun getAddress():Response<GetSavedAddresss>

    @POST("api/v1/rider/auth/savedPlaces/{uiid}")
    suspend fun updateAddress(@Path("uiid") identifier: String, @Body data: UpdateAddress): Response<GeneralResponse>

    @POST("api/v1/rider/auth/payment-method/create")
    suspend fun addCard(@Body data: AddCard): Response<GeneralResponse>

    @GET("api/v1/rider/auth/payment-method")
    suspend fun getCards(): Response<GetCardResponse>

    @POST("api/v1/rider/auth/requestRide")
    suspend fun makeARideRequest(@Body data: RideRequest): Response<ConfirmRideResponse>

    @DELETE("api/v1/rider/auth/payment-method/{uiid}/remove")
    suspend fun deleteCard(@Path("uiid") identifier: String): Response<GeneralResponse>

}