package com.mevron.rides.rider.myrides.data.model

data class AllTripsSuccess(
    val `data`: List<AllTripsData>,
    val message: String,
    val status: String
)