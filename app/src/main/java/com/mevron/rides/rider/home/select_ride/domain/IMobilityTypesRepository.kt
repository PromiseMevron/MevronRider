package com.mevron.rides.rider.home.select_ride.domain

import com.mevron.rides.rider.domain.DomainModel

interface IMobilityTypesRepository {

   suspend fun getMobilityTypesData(): DomainModel
}