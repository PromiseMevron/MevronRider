package com.mevron.rides.rider.myrides.domain.repository

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.savedplaces.data.model.SaveAddressRequest
import com.mevron.rides.rider.savedplaces.data.model.UpdateAddress

interface IMyRideRepo {
    suspend fun getScheduledRide(): DomainModel
    suspend fun addAllRides(): DomainModel
    suspend fun getASpecificRide(identifier: String): DomainModel
}