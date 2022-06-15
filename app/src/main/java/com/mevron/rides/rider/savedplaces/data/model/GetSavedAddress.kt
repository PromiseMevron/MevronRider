package com.mevron.rides.rider.savedplaces.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetSavedAddress(
    val success: GetAddSuccess
)
data class GetAddSuccess(
    @SerializedName("data")
    val addData: List<GetAddressData>,
    val message: String,
    val status: String
)

@Parcelize
data class GetAddressData(
    val address: String,
    val latitude: String,
    val longitude: String,
    val name: String,
    val type: String,
    val uuid: String
): Parcelable
