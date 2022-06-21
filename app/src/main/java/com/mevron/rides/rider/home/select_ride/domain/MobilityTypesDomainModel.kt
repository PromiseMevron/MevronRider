package com.mevron.rides.rider.home.select_ride.domain

data class MobilityTypesDomainModel(
    val mobilityTypes: List<MobilityTypeData>
)

class MobilityTypeData(
    val currency: String,
    val dropOffTime: String,
    val fare: String,
    val id: Int,
    val image: String,
    val name: String,
    val seats: Int,
    val type: String
)