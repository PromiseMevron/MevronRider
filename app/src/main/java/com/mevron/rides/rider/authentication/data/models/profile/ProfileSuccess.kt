package com.mevron.rides.rider.authentication.data.models.profile

import com.google.gson.annotations.SerializedName

data class ProfileSuccess(
    @SerializedName("data")
    val profileData: ProfileData,
    val message: String,
    val status: String
)