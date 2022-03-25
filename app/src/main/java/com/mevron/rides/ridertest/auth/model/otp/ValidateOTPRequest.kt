package com.mevron.rides.ridertest.auth.model.otp

data class ValidateOTPRequest(
    val code: String,
    val phoneNumber: String
)