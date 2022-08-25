package com.mevron.rides.rider.myrides.data.model.aTripModel

import com.google.gson.annotations.SerializedName

data class Success(
    @SerializedName("data")
    val tripData: TripData,
    val message: String,
    val status: String
)