package com.mevron.rides.rider.home.model.schedule

data class ScheduleRequest(
    val destinationAddress: String,
    val destinationLatitude: String,
    val destinationLongitude: String,
    val paymentMethod: String,
    val pickupAddress: String,
    val pickupLatitude: String,
    val pickupLongitude: String,
    val scheduleTime: String,
    val type: String,
    val vehicleType: String
)