package com.mevron.rides.rider.authentication.data.models.verifyotp

data class OTPData(
    val accessToken: String,
    val riderType: String,
    val stage: String,
    val uuid: String
)
