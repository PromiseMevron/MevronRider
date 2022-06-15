package com.mevron.rides.rider.emerg.domain.usecase

import com.mevron.rides.rider.emerg.domain.repository.IEmergencyRepository
import javax.inject.Inject

class GetContactUseCase @Inject constructor(private val repository: IEmergencyRepository) {
    suspend operator fun invoke() = repository.getEmergencyContact()
}