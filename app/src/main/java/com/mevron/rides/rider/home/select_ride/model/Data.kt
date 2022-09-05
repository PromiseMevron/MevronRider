package com.mevron.rides.rider.home.select_ride.model

data class Data(
    val currency: String,
    val dropOffTime: String,
    val fare: String,
    val fare2: List<Fare2>,
    val id: Int,
    val image: String,
    val name: String,
    val seats: Int,
    val type: String
)