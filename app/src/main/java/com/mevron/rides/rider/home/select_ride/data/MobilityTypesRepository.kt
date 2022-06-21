package com.mevron.rides.rider.home.select_ride.data

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.home.select_ride.domain.IMobilityTypesRepository

class MobilityTypesRepository : IMobilityTypesRepository {

    override suspend fun getMobilityTypesData(): DomainModel {
        // TODO Will be implemented subsequently
        return DomainModel.Success(Unit)
    }
}