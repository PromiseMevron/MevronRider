package com.mevron.rides.rider.authentication.domain.model

data class SaveDetailsRequest(
    val email: String,
    val lastName: String,
    val firstName: String,
    var phoneNumber: String? = null
)
