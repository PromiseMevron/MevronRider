package com.mevron.rides.rider.myrides.data.repository

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.myrides.data.model.AllTripsResponse
import com.mevron.rides.rider.myrides.data.model.aTripModel.TripDetailResponse
import com.mevron.rides.rider.myrides.data.network.MyRideAPI
import com.mevron.rides.rider.myrides.domain.model.AllTripsResult
import com.mevron.rides.rider.myrides.domain.model.GetAllTripsDomainData
import com.mevron.rides.rider.myrides.domain.model.TripDetailDomainData
import com.mevron.rides.rider.myrides.domain.repository.IMyRideRepo
import com.mevron.rides.rider.util.toReadableDate


class MyRideRepository(private val api: MyRideAPI) : IMyRideRepo {

    override suspend fun getScheduledRide(): DomainModel =
        api.getAllTrips().let {
            if (it.isSuccessful) {
                it.body()?.toDomainModel() ?: DomainModel.Error(Throwable("Trips not found"))
            } else {
                DomainModel.Error(Throwable(it.errorBody().toString()))
            }
        }

    override suspend fun addAllRides(): DomainModel =
        api.getAllTrips().let {
            if (it.isSuccessful) {
                it.body()?.toDomainModel() ?: DomainModel.Error(Throwable("Trips not found"))
            } else {
                DomainModel.Error(Throwable(it.errorBody().toString()))
            }
        }

    override suspend fun getASpecificRide(identifier: String): DomainModel =
        api.getTripDetail(identifier).let {
            if (it.isSuccessful) {
                it.body()?.toDomainModel() ?: DomainModel.Error(Throwable("Trips not found"))
            } else {
                DomainModel.Error(Throwable(it.errorBody().toString()))
            }
        }

    private fun AllTripsResponse.toDomainModel() = DomainModel.Success(
        data = GetAllTripsDomainData(data = this.success.data.map {
            AllTripsResult(
                amount = it.amount,
                time = it.time,
                date = it.date,
                id = it.id,
                status = it.status,
                title = it.title,
                vehicleName = it.vehicleName,
                vehicleNum = it.plateNumber
            )
        })
    )

    private fun TripDetailResponse.toDomainModel() = DomainModel.Success(
        data = this.success.tripData.apply {
            TripDetailDomainData(
                dateAndTime = "${this.trip.createdAt.toReadableDate()} ${this.trip.endTime}",
                carNumber = "",
                departureTime = this.trip.startTime,
                arrivalTime = this.trip.endTime,
                departureAddress = this.trip.pickupAddress,
                arrivalAddress = this.trip.destinationAddress,
                driverName = "${this.driver.firstName} ${this.driver.lastName}",
                driverRating = this.driver.rating ?: "",
                paymentType = this.trip.paymentMethod,
                driverProfile = this.driver.profilePicture ?: "",
                startLat = this.trip.pickupLatitude,
                startLng = this.trip.pickupLongitude,
                endLat = this.trip.destinationLatitude,
                endLng = this.trip.destinationLongitude,
                total = "${this.trip.currencyCode} ${this.trip.amount}"
            )
        }


    )

}

