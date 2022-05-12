package com.mevron.rides.rider.home.ride.model.rideconfirm

data class Trip(
    val amount: String,
    val createdAt: String,
    val destinationAddress: String,
    val destinationLatitude: String,
    val destinationLongitude: String,
    val driver_id: String,
    val endTime: String,
    val estimatedDistance: String,
    val id: Int,
    val maxEstimatedCost: String,
    val minEstimatedCost: String,
    val paymentMethod: String,
    val pickupAddress: String,
    val pickupLatitude: String,
    val pickupLongitude: String,
    val rider_id: String,
    val startTime: String,
    val status: String,
    val updatedAt: String,
    val uuid: String,
    val vehicleType: String,
    val verificationCode: String
)