package com.mevron.rides.rider.authentication.data.models.registerphone

import com.google.gson.annotations.SerializedName


data class RegisterPhoneSuccess(
    @SerializedName("data")
    val registerdata: RegisterData,
    val message: String,
    val status: String
)
