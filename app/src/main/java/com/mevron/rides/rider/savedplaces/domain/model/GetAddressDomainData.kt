package com.mevron.rides.rider.savedplaces.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class GetAddressDomainData(val savedAddresses: List<GetSavedAddressData>)

@Parcelize
data class GetSavedAddressData(
    val latitude: String,
    val longitude: String,
    val type: String,
    val address:String,
    val uuid: String,
    val name: String,
    val lat: String,
    var lng: String
): Parcelable
