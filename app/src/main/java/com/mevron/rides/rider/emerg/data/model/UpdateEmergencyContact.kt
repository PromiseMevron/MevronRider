package com.mevron.rides.rider.emerg.data.model

data class UpdateEmergencyContact(
    val details: List<Int>,
    val name: String,
    val phoneNumber: String
)