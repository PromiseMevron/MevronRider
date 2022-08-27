package com.mevron.rides.rider.settings.referal.data.network

import com.mevron.rides.rider.remote.model.GeneralResponse
import com.mevron.rides.rider.settings.referal.data.model.GetReferalDetail
import com.mevron.rides.rider.settings.referal.data.model.GetReferalHistory
import com.mevron.rides.rider.settings.referal.data.model.ReferalReport
import com.mevron.rides.rider.settings.referal.data.model.SetReferal
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ReferralApi {

    @GET("api/v1/rider/auth/referral-history")
    suspend fun getReferralHistory(): Response<GetReferalHistory>

    @POST("api/v1/rider/auth/referral-code")
    suspend fun setReferral(@Body data: SetReferal): Response<GeneralResponse>

    @POST("api/v1/rider/auth/referral-report")
    suspend fun getReferralReport(@Body data: ReferalReport): Response<GetReferalDetail>
}