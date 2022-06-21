package com.mevron.rides.rider.home.select_ride.model

import com.google.gson.annotations.SerializedName

data class Success(
    @SerializedName("data")
    val data: List<Data>,
    val message: String,
    val status: String
)