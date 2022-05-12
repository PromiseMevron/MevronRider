package com.mevron.rides.rider.savedplaces.data.network

import com.mevron.rides.rider.remote.model.GeneralResponse
import com.mevron.rides.rider.savedplaces.data.model.GetSavedAddress
import com.mevron.rides.rider.savedplaces.data.model.SaveAddressRequest
import com.mevron.rides.rider.savedplaces.data.model.UpdateAddress
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AddressAPI {
    @POST("api/v1/rider/auth/savedPlaces")
    suspend fun saveAddress(@Body data: SaveAddressRequest): Response<GeneralResponse>

    @GET("api/v1/rider/auth/savedPlaces")
    suspend fun getAddress(): Response<GetSavedAddress>

    @POST("api/v1/rider/auth/savedPlaces/{uiid}")
    suspend fun updateAddress(@Path("uiid") identifier: String, @Body data: UpdateAddress): Response<GeneralResponse>

}