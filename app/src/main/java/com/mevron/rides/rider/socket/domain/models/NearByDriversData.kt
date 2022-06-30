package com.mevron.rides.rider.socket.domain.models

import com.google.gson.annotations.SerializedName

class NearByDriversData(
    @SerializedName("locations")
    val locations: List<NearByDriver>
)

data class NearByDriver(
    @SerializedName("id")
    val id: String,
    @SerializedName("driver_id")
    val driverId: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("direction")
    val direction: String
)