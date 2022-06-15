package com.mevron.rides.rider.remote.model

data class RideRequest(
    val cardId: String? = null,
    val destinationAddress: String,
    val destinationLatitude: String,
    val destinationLongitude: String,
    val paymentMethod: String,
    val pickupAddress: String,
    val pickupLatitude: String,
    val pickupLongitude: String,
    val vehicleType: String
)