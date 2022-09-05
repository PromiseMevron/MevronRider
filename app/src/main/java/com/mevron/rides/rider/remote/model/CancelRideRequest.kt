package com.mevron.rides.rider.remote.model

data class CancelRideRequest (
    val trip_id: String,
    val comment: String = "others"
)