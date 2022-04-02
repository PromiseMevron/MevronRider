package com.mevron.rides.ridertest.settings.emerg.model

data class UpdateEmergencyContact(
    val details: List<Int>,
    val name: String,
    val phoneNumber: String
)