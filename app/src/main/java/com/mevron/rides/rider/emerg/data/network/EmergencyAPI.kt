package com.mevron.rides.rider.emerg.data.network

import com.mevron.rides.rider.emerg.data.model.AddContactRequest
import com.mevron.rides.rider.emerg.data.model.GetContactsResponse
import com.mevron.rides.rider.remote.model.GeneralResponse
import com.mevron.rides.rider.emerg.data.model.UpdateEmergencyContact
import retrofit2.Response
import retrofit2.http.*

interface EmergencyAPI {

    @POST("api/v1/rider/auth/emergency-contacts")
    suspend fun saveEmergency(@Body data: AddContactRequest): Response<GeneralResponse>

    @GET("api/v1/rider/auth/emergency-contacts")
    suspend fun getEmergency(): Response<GetContactsResponse>

    @POST("api/v1/rider/auth/emergency-contacts/{uiid}")
    suspend fun updateEmergency(
        @Path("uiid") id: String,
        data: UpdateEmergencyContact
    ): Response<GeneralResponse>

    @DELETE("api/v1/rider/auth/emergency-contacts/{uiid}")
    suspend fun deleteEmergency(@Path("uiid") id: String): Response<GeneralResponse>
}