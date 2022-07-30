package com.mevron.rides.rider.socket.domain.models

data class Driver(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val profilePicture: String,
    val rating: String,
    val vehicle: Vehicle
)