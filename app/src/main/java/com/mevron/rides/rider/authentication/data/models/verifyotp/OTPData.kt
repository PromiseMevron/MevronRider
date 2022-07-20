package com.mevron.rides.rider.authentication.data.models.verifyotp

import com.google.gson.annotations.SerializedName

data class OTPData(
    val accessToken: String,
    @SerializedName("type")
    val riderType: String,
    val stage: String,
    val uuid: String
)
