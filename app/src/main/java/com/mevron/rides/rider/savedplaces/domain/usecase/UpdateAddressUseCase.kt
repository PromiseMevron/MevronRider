package com.mevron.rides.rider.savedplaces.domain.usecase

import com.mevron.rides.rider.savedplaces.data.model.UpdateAddress
import com.mevron.rides.rider.savedplaces.domain.repository.IAddressRepository
import javax.inject.Inject

class UpdateAddressUseCase @Inject constructor(private val repository: IAddressRepository) {
    suspend operator fun invoke(identifier: String, data: UpdateAddress) =
        repository.updateAnAddress(identifier = identifier, data = data)
}