package com.mevron.rides.rider.domain.usecase

import com.mevron.rides.rider.domain.IOpenBookedStateRepository
import javax.inject.Inject

class GetOpenBookUseCase @Inject constructor(
    private val repository: IOpenBookedStateRepository
) {
    operator fun invoke() = repository.currentTripState
}