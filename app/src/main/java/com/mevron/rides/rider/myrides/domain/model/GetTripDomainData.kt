package com.mevron.rides.rider.myrides.domain.model

import com.mevron.rides.rider.myrides.data.model.AllTripsData

data class GetTripDomainData (
    val amount: String = "",
    val endDate: String = "",
    val online: String = "",
    val results: List<AllTripsData> = mutableListOf(),
    val rides: Int = 0,
    val startDate: String = "")


