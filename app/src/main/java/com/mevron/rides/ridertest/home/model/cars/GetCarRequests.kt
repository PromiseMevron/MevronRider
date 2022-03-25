package com.mevron.rides.ridertest.home.model.cars

data class GetCarRequests(
    val destinationAddress: String,
    val destinationLatitude: String,
    val destinationLongitude: String,
    val pickupAddress: String,
    val pickupLatitude: String,
    val pickupLongitude: String
)