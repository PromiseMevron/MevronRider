package com.mevron.rides.rider.authentication.domain.model

data class RegisterPhoneRequest(
    val country: String,
    val phoneNumber: String
)
