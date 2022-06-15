package com.mevron.rides.rider.emerg.domain.usecase

import com.mevron.rides.rider.emerg.domain.repository.IEmergencyRepository
import com.mevron.rides.rider.emerg.data.model.UpdateEmergencyContact
import javax.inject.Inject

class UpdateContactUseCase @Inject constructor(private val repository: IEmergencyRepository) {
    suspend operator fun invoke(id: String, data: UpdateEmergencyContact) =
        repository.updateEmergencyContact(id, data)
}