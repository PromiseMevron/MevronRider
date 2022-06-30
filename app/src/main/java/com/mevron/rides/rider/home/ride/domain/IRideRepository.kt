package com.mevron.rides.rider.home.ride.domain

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.remote.model.RideRequest

interface IRideRepository {
    suspend fun createRide(rideRequest: RideRequest): DomainModel
}