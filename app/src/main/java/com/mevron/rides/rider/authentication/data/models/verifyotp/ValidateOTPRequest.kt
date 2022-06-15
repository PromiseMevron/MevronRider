package com.mevron.rides.rider.authentication.data.models.verifyotp

data class ValidateOTPRequest(
    val code: String,
    val phoneNumber: String
)