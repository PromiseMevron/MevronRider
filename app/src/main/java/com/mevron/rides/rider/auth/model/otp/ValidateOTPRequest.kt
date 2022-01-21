package com.mevron.rides.rider.auth.model.otp

data class ValidateOTPRequest(
    val code: String,
    val phoneNumber: String
)