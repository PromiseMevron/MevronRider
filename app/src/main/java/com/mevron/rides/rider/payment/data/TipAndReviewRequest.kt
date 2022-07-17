package com.mevron.rides.rider.payment.data

import com.google.gson.annotations.SerializedName

data class TipAndReviewRequest(
    @SerializedName("trip_id")
    val tripId: String,
    @SerializedName("amount")
    val tipAmount: Int,
    @SerializedName("comment")
    val comment: List<String>
)