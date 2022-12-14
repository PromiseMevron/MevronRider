package com.mevron.rides.rider.home.model.request

data class Data(
    val amount: Int,
    val createdAt: String,
    val destinationAddress: String,
    val destinationLatitude: String,
    val destinationLongitude: String,
    val endTime: String,
    val estimatedDistance: Int,
    val id: Int,
    val maxEstimatedCost: Int,
    val minEstimatedCost: Int,
    val paymentMethod: String,
    val pickupAddress: String,
    val pickupLatitude: String,
    val pickupLongitude: String,
    val rider_id: Int,
    val startTime: String,
    val status: String,
    val updatedAt: String,
    val uuid: String,
    val vehicleType: String,
    val verificationCode: String
)