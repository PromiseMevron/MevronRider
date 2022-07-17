package com.mevron.rides.rider.payment.data

import com.google.gson.annotations.SerializedName

data class RateRequest(
    @SerializedName("trip_id")
    val tripId: String,
    @SerializedName("rating")
    val rating: Int
)