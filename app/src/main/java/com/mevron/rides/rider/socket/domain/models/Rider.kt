package com.mevron.rides.rider.socket.domain.models

data class Rider(
    val firstName: String,
    val lastName: String,
    val profilePicture: Any,
    val rating: String
)