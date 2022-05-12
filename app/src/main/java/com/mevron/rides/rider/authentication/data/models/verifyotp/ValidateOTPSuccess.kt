package com.mevron.rides.rider.authentication.data.models.verifyotp

import com.google.gson.annotations.SerializedName

data class ValidateOTPSuccess(
    @SerializedName("data")
    val otpData: OTPData,
    val message: String,
    val status: String
)
