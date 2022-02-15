package com.mevron.rides.rider.home.model.getAddress

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Data(
    val address: String,
    val latitude: String,
    val longitude: String,
    val name: String,
    val type: String,
    val uuid: String
): Parcelable