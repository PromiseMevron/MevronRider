package com.mevron.rides.rider.socket.domain.models

data class Trip(
    val destinationAddress: String,
    val destinationLatitude: String,
    val destinationLongitude: String,
    val estimatedPickupTime: Any,
    val pickupAddress: String,
    val pickupLatitude: String,
    val pickupLongitude: String,
    val tripId: String,
    val verificationCode: String
)