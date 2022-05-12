package com.mevron.rides.rider.authentication.domain.model


data class ValidateOtpRequest(
    val code: String,
    val phoneNumber: String
)
