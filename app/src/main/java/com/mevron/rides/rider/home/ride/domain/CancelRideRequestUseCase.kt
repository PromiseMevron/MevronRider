package com.mevron.rides.rider.home.ride.domain

import com.mevron.rides.rider.remote.model.CancelRideRequest
import javax.inject.Inject

class CancelRideRequestUseCase @Inject constructor(
    private val repo: IRideRepository
) {
    suspend operator fun invoke(rideRequest: CancelRideRequest) = repo.cancelRide(rideRequest)
}