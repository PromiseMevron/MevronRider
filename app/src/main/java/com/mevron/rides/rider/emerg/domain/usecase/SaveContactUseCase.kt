package com.mevron.rides.rider.emerg.domain.usecase

import com.mevron.rides.rider.emerg.data.model.AddContactRequest
import com.mevron.rides.rider.emerg.domain.repository.IEmergencyRepository
import javax.inject.Inject

class SaveContactUseCase @Inject constructor(private val repository: IEmergencyRepository) {
    suspend operator fun invoke(data: AddContactRequest) = repository.saveEmergencyContact(data)
}