package com.mevron.rides.rider.authentication.domain.model

data class VerifyOTPDomainModel(
    val accessToken: String,
    val riderType: String,
    val uuid: String
)
