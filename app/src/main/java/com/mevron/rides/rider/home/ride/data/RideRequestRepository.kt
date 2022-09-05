package com.mevron.rides.rider.home.ride.data

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.home.ride.domain.IRideRepository
import com.mevron.rides.rider.home.ride.domain.RideDomainData
import com.mevron.rides.rider.home.ride.model.ConfirmRideResponse
import com.mevron.rides.rider.remote.model.CancelRideRequest
import com.mevron.rides.rider.remote.model.RideRequest

class RideRequestRepository(private val api: RideRequestApi) : IRideRepository {
    override suspend fun createRide(rideRequest: RideRequest): DomainModel {
        val result = api.makeARideRequest(rideRequest)
        return if (result.isSuccessful) {
            result.body()?.let { data ->
                DomainModel.Success(data = data.toDomainModel())
            } ?: DomainModel.Error(Throwable("Error, Ride request response is empty"))
        } else {
            DomainModel.Error(Throwable(result.errorBody().toString()))
        }
    }

    override suspend fun cancelRide(rideRequest: CancelRideRequest): DomainModel {
        val result = api.cancelARideRequest(rideRequest)
        return if (result.isSuccessful) {
            result.body()?.let { data ->
                DomainModel.Success(data = Unit)
            } ?: DomainModel.Error(Throwable("Error, Ride request response is empty"))
        } else {
            DomainModel.Error(Throwable(result.errorBody().toString()))
        }
    }
}

private fun ConfirmRideResponse.toDomainModel() = this.success.data.let {
    RideDomainData(
        amount = it.amount,
        createdAt = it.createdAt,
        destinationAddress = it.destinationAddress,
        destinationLatitude = it.destinationLatitude,
        destinationLongitude = it.destinationLongitude,
        endTime = it.endTime,
        estimatedDistance = it.estimatedDistance,
        id = it.id,
        maxEstimatedCost = it.maxEstimatedCost,
        minEstimatedCost = it.minEstimatedCost,
        paymentMethod = it.paymentMethod,
        pickupAddress = it.pickupAddress,
        pickupLatitude = it.pickupLatitude,
        pickupLongitude = it.pickupLongitude,
        riderId = it.rider_id,
        startTime = it.startTime,
        status = it.status,
        updatedAt = it.updatedAt,
        uuid = it.uuid,
        vehicleType = it.vehicleType,
        verificationCode = it.verificationCode
    )
}