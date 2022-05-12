package com.mevron.rides.rider.home.model.cars

data class Ride(
    val currency: String,
    val dropOffTime: String,
    val fare: String,
    val id: Int,
    val image: String,
    val name: String,
    val seats: Int,
    val type: String
)