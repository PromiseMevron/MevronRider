package com.mevron.rides.ridertest.auth.model.otp

data class Data(
    val accessToken: String,
    val riderType: String,
    val stage: String,
    val uuid: String
)