package com.mevron.rides.rider.home.select_ride.model

data class Success(
    val `data`: List<Data>,
    val message: String,
    val status: String
)