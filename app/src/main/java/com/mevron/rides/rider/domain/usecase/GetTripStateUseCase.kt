package com.mevron.rides.rider.domain.usecase

import com.mevron.rides.rider.domain.ITripStateRepository
import javax.inject.Inject

class GetTripStateUseCase @Inject constructor(
    private val repository: ITripStateRepository
) {
    operator fun invoke() = repository.currentTripState
}