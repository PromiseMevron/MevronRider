package com.mevron.rides.rider.authentication.data.models.createaccount

data class SaveDetailsRequest(
    val email: String,
    val lastName: String,
    val firstName: String,
    val country: String?,
    var phoneNumber: String? = null
)