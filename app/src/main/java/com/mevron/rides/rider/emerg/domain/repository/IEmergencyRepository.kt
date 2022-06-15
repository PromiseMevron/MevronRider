package com.mevron.rides.rider.emerg.domain.repository

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.emerg.data.model.AddContactRequest
import com.mevron.rides.rider.emerg.data.model.UpdateEmergencyContact

interface IEmergencyRepository {
    suspend fun getEmergencyContact(): DomainModel
    suspend fun updateEmergencyContact(
        id: String,
        data: UpdateEmergencyContact
    ): DomainModel
    suspend fun saveEmergencyContact(data: AddContactRequest): DomainModel
    suspend fun deleEmergencyContact(id: String): DomainModel
}