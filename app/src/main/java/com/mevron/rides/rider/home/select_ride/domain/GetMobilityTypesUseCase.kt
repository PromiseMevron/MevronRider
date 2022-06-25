package com.mevron.rides.rider.home.select_ride.domain

import javax.inject.Inject

class GetMobilityTypesUseCase @Inject constructor(
    private val repository: IMobilityTypesRepository
) {
    suspend operator fun invoke(request: MobilityTypesRequest) =
        repository.getMobilityTypesData(request)
}