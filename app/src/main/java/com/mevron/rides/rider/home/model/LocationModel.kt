package com.mevron.rides.rider.home.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationModel(
    val lat: Double,
    val lng: Double,
    val address: String
): Parcelable
