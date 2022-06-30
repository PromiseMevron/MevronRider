package com.mevron.rides.rider.socket.domain.models

import com.google.gson.annotations.SerializedName

data class StateMachineData(
    @SerializedName("current_state")
    val currentState: String,
    @SerializedName("meta_data")
    val metaData: StateMachineMetaData
)

data class StateMachineMetaData(
    @SerializedName("trip_id")
    val tripId: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("pickupLatitude")
    val pickupLatitude: String,
    @SerializedName("pickupLongitude")
    val pickupLongitude: String,
    @SerializedName("destinationLatitude")
    val destinationLatitude: String,
    @SerializedName("destinationLongitude")
    val destinationLongitude: String
)