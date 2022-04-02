package com.mevron.rides.ridertest.remote

import com.mevron.rides.ridertest.auth.model.details.SaveDetailsRequest
import com.mevron.rides.ridertest.auth.model.details.SaveResponse
import com.mevron.rides.ridertest.auth.model.otp.OTPResponse
import com.mevron.rides.ridertest.auth.model.otp.ValidateOTPRequest
import com.mevron.rides.ridertest.auth.model.register.RegisterBody
import com.mevron.rides.ridertest.auth.model.register.RegisterResponse
import com.mevron.rides.ridertest.home.model.AddCard
import com.mevron.rides.ridertest.home.model.cars.GetCarRequests
import com.mevron.rides.ridertest.home.model.cars.GetCarsCategory
import com.mevron.rides.ridertest.home.model.getAddress.GetSavedAddresss
import com.mevron.rides.ridertest.home.model.getAddress.SaveAddressRequest
import com.mevron.rides.ridertest.home.model.getAddress.UpdateAddress
import com.mevron.rides.ridertest.home.model.getCard.GetCardResponse
import com.mevron.rides.ridertest.home.model.schedule.ScheduleRequest
import com.mevron.rides.ridertest.home.ride.model.ConfirmRideResponse
import com.mevron.rides.ridertest.home.select_ride.model.GetCarsCategory2
import com.mevron.rides.ridertest.remote.model.GeneralResponse
import com.mevron.rides.ridertest.remote.model.RideRequest
import com.mevron.rides.ridertest.remote.model.getprofile.GetProfileResponse
import com.mevron.rides.ridertest.settings.emerg.AddContactRequest
import com.mevron.rides.ridertest.settings.emerg.model.GetContactsResponse
import com.mevron.rides.ridertest.settings.emerg.model.UpdateEmergencyContact
import com.mevron.rides.ridertest.settings.emerg.model.pref.UpdatePrefrenceRequest
import com.mevron.rides.ridertest.settings.referal.model.GetReferalHistory
import com.mevron.rides.ridertest.settings.referal.model.ReferalReport
import com.mevron.rides.ridertest.settings.referal.model.SetReferal
import com.mevron.rides.ridertest.settings.referal.model.notif.NotificationResponse
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
    suspend fun getProfile(): Response<GetProfileResponse>

    @POST("api/v1/rider/auth/profile")
    suspend fun updateProfile(@Body data: SaveDetailsRequest): Response<SaveResponse>

    @POST("api/v1/rider/auth/resend-verification")
    suspend fun resendEmailLink(): Response<SaveResponse>



    @POST("api/v1/rider/auth/carCategories")
    suspend fun getCars(@Body data: GetCarRequests):Response<GetCarsCategory2>

    @POST("api/v1/rider/auth/savedPlaces")
    suspend fun saveAddress(@Body data: SaveAddressRequest):Response<GeneralResponse>

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

    @GET("api/v1/rider/auth/referral-history")
    suspend fun getReferralHistory(): Response<GetReferalHistory>

    @POST("api/v1/rider/auth/referral-history")
    suspend fun setReferral(data: SetReferal): Response<GeneralResponse>

    @POST("api/v1/rider/auth/referral-report")
    suspend fun getReferralReport(data: ReferalReport): Response<GeneralResponse>

    @DELETE("api/v1/rider/auth/payment-method/{uiid}/remove")
    suspend fun deleteCard(@Path("uiid") identifier: String): Response<GeneralResponse>

    @GET("api/v1/rider/auth/trips")
    suspend fun getAllTrips(): Response<GeneralResponse>

    @GET("api/v1/rider/auth/trips/{uiid}")
    suspend fun getTripDetail(@Path("uiid") id: String): Response<GeneralResponse>

    @POST("api/v1/rider/auth/scheduleRide")
    suspend fun scheduleARide(@Body data: ScheduleRequest): Response<ConfirmRideResponse>

    @POST("api/v1/rider/auth/emergency-contacts")
    suspend fun saveEmergency(@Body data: AddContactRequest): Response<GeneralResponse>

    @GET("api/v1/rider/auth/emergency-contacts")
    suspend fun getEmergency(): Response<GetContactsResponse>

    @POST("api/v1/rider/auth/emergency-contacts/{uiid}")
    suspend fun updateEmergency(@Path("uiid") id: String, data: UpdateEmergencyContact): Response<GeneralResponse>


    @DELETE("api/v1/rider/auth/emergency-contacts/{uiid}")
    suspend fun deleteEmergency(@Path("uiid") id: String): Response<GeneralResponse>

    @GET("/api/v1/rider/auth/promo-code")
    suspend fun getPromo(): Response<GeneralResponse>

    @GET("/api/v1/rider/auth/notifications?page=1")
    suspend fun getNotifications(): Response<NotificationResponse>









}