package com.mevron.rides.rider.myrides.data.repository

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.myrides.data.model.AllTripsResponse
import com.mevron.rides.rider.myrides.data.network.MyRideAPI
import com.mevron.rides.rider.myrides.domain.model.AllTripsResult
import com.mevron.rides.rider.myrides.domain.model.GetAllTripsDomainData
import com.mevron.rides.rider.myrides.domain.repository.IMyRideRepo


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

    private fun AllTripsResponse.toDomainModel() = DomainModel.Success(
        data = GetAllTripsDomainData(data = this.success.data.map {
            AllTripsResult(
                amount = it.amount,
                time = it.startTime,
                date = it.createdAt,
                id = it.id.toString(),
                status = it.status,
                title = it.vehicleType

            )
        })
    )

}

