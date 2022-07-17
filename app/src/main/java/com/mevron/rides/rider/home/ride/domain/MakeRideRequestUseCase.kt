package com.mevron.rides.rider.home.ride.domain

import com.mevron.rides.rider.remote.model.RideRequest
import javax.inject.Inject

class MakeRideRequestUseCase @Inject constructor(
    private val repo: IRideRepository
) {
    suspend operator fun invoke(rideRequest: RideRequest) = repo.createRide(rideRequest)
}