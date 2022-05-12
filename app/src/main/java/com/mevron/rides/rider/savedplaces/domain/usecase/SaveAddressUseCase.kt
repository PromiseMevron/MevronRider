package com.mevron.rides.rider.savedplaces.domain.usecase

import com.mevron.rides.rider.savedplaces.data.model.SaveAddressRequest
import com.mevron.rides.rider.savedplaces.domain.repository.IAddressRepository
import javax.inject.Inject

class SaveAddressUseCase @Inject constructor(private val repository: IAddressRepository) {
    suspend operator fun invoke(data: SaveAddressRequest) = repository.addAnAddress(data)
}