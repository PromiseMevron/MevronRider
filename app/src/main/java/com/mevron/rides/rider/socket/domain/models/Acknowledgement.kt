package com.mevron.rides.rider.socket.domain.models

import com.google.gson.annotations.SerializedName

data class Acknowledgement(
    @SerializedName("uuid")
    val uuid: String,
    @SerializedName("type")
    val type: String
)

data class OnConnected(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String
)