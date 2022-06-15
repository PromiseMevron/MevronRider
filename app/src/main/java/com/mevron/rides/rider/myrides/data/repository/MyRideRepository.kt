package com.mevron.rides.rider.myrides.data.repository

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.myrides.data.network.MyRideAPI
import com.mevron.rides.rider.myrides.domain.repository.IMyRideRepo


class MyRideRepository(private val api: MyRideAPI) : IMyRideRepo {

    override suspend fun getScheduledRide(): DomainModel {
        TODO("Not yet implemented")
    }

    override suspend fun addAllRides(): DomainModel {
        TODO("Not yet implemented")
    }

    override suspend fun getASpecificRide(identifier: String): DomainModel {
        TODO("Not yet implemented")
    }
}