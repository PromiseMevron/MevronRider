package com.mevron.rides.rider.home.select_ride.domain

data class MobilityTypesRequest(
    val destinationAddress: String,
    val destinationLatitude: String,
    val destinationLongitude: String,
    val pickupAddress: String,
    val pickupLatitude: String,
    val pickupLongitude: String
)