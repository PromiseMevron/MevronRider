package com.mevron.rides.rider.domain.usecase

import com.mevron.rides.rider.domain.ITripStateRepository
import com.mevron.rides.rider.domain.TripState
import javax.inject.Inject

class SetTripStateUseCase @Inject constructor(
    private val repository: ITripStateRepository
) {
    operator fun invoke(tripState: TripState) = repository.setTripState(tripState)
}