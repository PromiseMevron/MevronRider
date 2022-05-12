package com.mevron.rides.rider.savedplaces.data.model

data class SaveAddressRequest(
    val address: String,
    val latitude: String,
    val longitude: String,
    val name: String,
    val type: String
)
