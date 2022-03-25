package com.mevron.rides.ridertest.auth.model.details

data class SaveDetailsRequest(
    val email: String,
    val lastName: String,
    val firstName: String,
    var phoneNumber: String? = null
)