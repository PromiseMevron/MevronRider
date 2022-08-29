package com.mevron.rides.rider.home.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationModel(
    val lat: Double,
    val lng: Double,
    val address: String
): Parcelable {
    val isEmpty: Boolean
        get() = lat == Double.MIN_VALUE || lng == Double.MIN_VALUE

    companion object {
        val EMPTY = LocationModel(Double.MIN_VALUE, Double.MIN_VALUE, "")
    }
}

data class DriverLocationModel(
    val lat: Double,
    val long: Double,
    val direction: Float? = 0.0F,
){
    companion object {
        val EMPTY = DriverLocationModel(Double.MIN_VALUE, Double.MIN_VALUE, 0.0F)
    }
}
