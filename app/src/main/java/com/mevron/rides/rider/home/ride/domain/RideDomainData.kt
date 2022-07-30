package com.mevron.rides.rider.home.ride.domain

data class RideDomainData(
    val amount: Int,
    val createdAt: String,
    val destinationAddress: String,
    val destinationLatitude: String,
    val destinationLongitude: String,
    val endTime: String,
    val estimatedDistance: String,
    val id: Int,
    val maxEstimatedCost: Int,
    val minEstimatedCost: Int,
    val paymentMethod: String,
    val pickupAddress: String,
    val pickupLatitude: String,
    val pickupLongitude: String,
    val riderId: String,
    val startTime: String,
    val status: String,
    val updatedAt: String,
    val uuid: String,
    val vehicleType: String,
    val verificationCode: String
)
