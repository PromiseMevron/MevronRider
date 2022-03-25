package com.mevron.rides.ridertest.home.model.request

data class RideRequest(
    val destinationAddress: String,
    val destinationLatitude: String,
    val destinationLongitude: String,
    val paymentMethod: String,
    val pickupAddress: String,
    val pickupLatitude: String,
    val pickupLongitude: String,
    val vehicleType: String
)