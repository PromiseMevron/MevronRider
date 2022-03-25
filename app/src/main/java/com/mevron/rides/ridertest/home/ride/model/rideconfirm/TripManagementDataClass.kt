package com.mevron.rides.ridertest.home.ride.model.rideconfirm

data class TripManagementDataClass(
    val estimatedDistance: String,
    val estimatedPickupTime: String,
    val estimatedTripTime: String,
    val riderPicture: String,
    val riderRating: String,
    val trip: Trip
)