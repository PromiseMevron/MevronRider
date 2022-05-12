package com.mevron.rides.rider.supportpages.data.model

import com.google.gson.annotations.SerializedName

data class PromoSuccess(
    @SerializedName("data")
    val promoData: List<PromoData>,
    val message: String,
    val status: String
)