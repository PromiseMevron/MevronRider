package com.mevron.rides.rider.myrides.data.repository

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.myrides.data.model.AllTripsResponse
import com.mevron.rides.rider.myrides.data.model.aTripModel.TripDetailResponse
import com.mevron.rides.rider.myrides.data.network.MyRideAPI
import com.mevron.rides.rider.myrides.domain.model.AllTripsResult
import com.mevron.rides.rider.myrides.domain.model.GetAllTripsDomainData
import com.mevron.rides.rider.myrides.domain.model.TripDetailDomainData
import com.mevron.rides.rider.myrides.domain.repository.IMyRideRepo
import com.mevron.rides.rider.remote.HTTPErrorHandler
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.toReadableDate


class MyRideRepository(private val api: MyRideAPI) : IMyRideRepo {

    override suspend fun getScheduledRide(): DomainModel =
        api.getAllTrips().let {
            if (it.isSuccessful) {
                it.body()?.toDomainModel() ?: DomainModel.Error(Throwable(Constants.UNEXPECTED_ERROR))
            } else {
                val error = HTTPErrorHandler.handleErrorWithCode(it)
                DomainModel.Error(Throwable(error?.error?.message ?: Constants.UNEXPECTED_ERROR))
            }
        }

    override suspend fun addAllRides(): DomainModel =
        api.getAllTrips().let {
            if (it.isSuccessful) {
                it.body()?.toDomainModel() ?: DomainModel.Error(Throwable(Constants.UNEXPECTED_ERROR))
            } else {
                val error = HTTPErrorHandler.handleErrorWithCode(it)
                DomainModel.Error(Throwable(error?.error?.message ?: Constants.UNEXPECTED_ERROR))
            }
        }

    override suspend fun getASpecificRide(identifier: String): DomainModel =
        api.getTripDetail(identifier).let {
            if (it.isSuccessful) {
                it.body()?.toDomainModel() ?: DomainModel.Error(Throwable(Constants.UNEXPECTED_ERROR))
            } else {
                val error = HTTPErrorHandler.handleErrorWithCode(it)
                DomainModel.Error(Throwable(error?.error?.message ?: Constants.UNEXPECTED_ERROR))
            }
        }

    private fun AllTripsResponse.toDomainModel() = DomainModel.Success(
        data = GetAllTripsDomainData(data = this.success.data.map {
            AllTripsResult(
                amount = "${it.currency}${it.amount}",
                time = it.time,
                date = it.date,
                id = it.id,
                status = it.status,
                title = it.title,
                vehicleName = it.vehicleName,
                vehicleNum = it.plateNumber,
                destinationLatitude = it.destinationLatitude,
                destinationLongitude = it.destinationLongitude,
                pickupLatitude = it.pickupLatitude,
                pickupLongitude = it.pickupLongitude,
                driverProfile = it.riderPicture,
                polyLine = it.polyLine
            )
        })
    )

    private fun TripDetailResponse.toDomainModel(): DomainModel{
        val dt = this.success.tripData.let {
            TripDetailDomainData(
                dateAndTime = "${it.trip.createdAt.toReadableDate()} ${it.trip.endTime}",
                carNumber = "",
                departureTime = it.trip.startTime,
                arrivalTime = it.trip.endTime,
                departureAddress = it.trip.pickupAddress,
                arrivalAddress = it.trip.destinationAddress,
                driverName = "${it.driver.firstName} ${it.driver.lastName}",
                driverRating = it.driver.rating ?: "",
                paymentType = it.trip.paymentMethod,
                driverProfile = it.driver.profilePicture ?: "",
                startLat = it.trip.pickupLatitude,
                startLng = it.trip.pickupLongitude,
                endLat = it.trip.destinationLatitude,
                endLng = it.trip.destinationLongitude,
                total = "${it.trip.currencyCode} ${it.trip.amount}",
                polyLine = it.trip.polyLine
            )
        }
        return DomainModel.Success(
            data = dt
        )
    }

}

