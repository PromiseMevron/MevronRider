package com.mevron.rides.rider.settings.referal.data.model

import com.google.gson.annotations.SerializedName

data class GetReferalDetail (
    val success: GetReferalDetailSuccess
)

data class GetReferalDetailSuccess(
    @SerializedName("data")
    val refData: GetReferalDetailData,
    val message: String,
    val status: String
)
data class GetReferalDetailData(
    val rides: String
)
