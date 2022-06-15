package com.mevron.rides.rider.home.model.getAddress

data class UpdateAddress(
    val address: String,
    val latitude: String,
    val longitude: String,
    val name: String,
    val type: String
)