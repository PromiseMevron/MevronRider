package com.mevron.rides.rider.auth.model.details

data class SaveDetailsRequest(
    val email: String,
    val lastName: String,
    val firstName: String
)