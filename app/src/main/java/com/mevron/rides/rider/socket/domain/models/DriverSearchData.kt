package com.mevron.rides.rider.socket.domain.models

import com.google.gson.annotations.SerializedName

data class DriverSearchData(
    @SerializedName("tripId")
    val tripId: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String
)