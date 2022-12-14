package com.mevron.rides.rider.settings.emerg.model.pref

data class UpdatePrefrenceRequest(
    val acceptCash: Int,
    val acceptTrips: Int,
    val excludeLowRated: Int,
    val longDistance: Int,
    val preferredNavigation: String
)