package com.mevron.rides.rider.home.ride.domain

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.remote.model.CancelRideRequest
import com.mevron.rides.rider.remote.model.RideRequest

interface IRideRepository {
    suspend fun createRide(rideRequest: RideRequest): DomainModel
    suspend fun cancelRide(rideRequest: CancelRideRequest): DomainModel
}