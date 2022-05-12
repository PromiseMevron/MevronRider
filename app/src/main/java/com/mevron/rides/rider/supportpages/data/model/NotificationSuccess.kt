package com.mevron.rides.rider.supportpages.data.model

import com.google.gson.annotations.SerializedName

data class NotificationSuccess(
    @SerializedName("data")
    val notificationData: NotificationData,
    val message: String,
    val status: String
)