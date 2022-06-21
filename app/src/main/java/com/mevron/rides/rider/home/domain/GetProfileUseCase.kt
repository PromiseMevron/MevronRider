package com.mevron.rides.rider.home.domain

import javax.inject.Inject

class GetProfileUseCase @Inject constructor(private val repository: IProfileRepository) {
    suspend operator fun invoke() = repository.getProfile()
}