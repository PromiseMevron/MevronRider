package com.mevron.rides.rider.savedplaces.domain.repository

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.savedplaces.data.model.SaveAddressRequest
import com.mevron.rides.rider.savedplaces.data.model.UpdateAddress

interface IAddressRepository {

    suspend fun getSavedAddresses(): DomainModel
    suspend fun addAnAddress(data: SaveAddressRequest): DomainModel
    suspend fun updateAnAddress(identifier: String, data: UpdateAddress): DomainModel
}