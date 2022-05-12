package com.mevron.rides.rider.savedplaces.domain.usecase

import com.mevron.rides.rider.savedplaces.domain.repository.IAddressRepository
import javax.inject.Inject

class GetAddressUseCase @Inject constructor(private val repository: IAddressRepository) {
    suspend operator fun invoke() = repository.getSavedAddresses()
}