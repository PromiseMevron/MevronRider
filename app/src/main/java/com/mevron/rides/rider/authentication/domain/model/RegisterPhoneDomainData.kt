package com.mevron.rides.rider.authentication.domain.model

import com.mevron.rides.rider.authentication.data.models.registerphone.RegisterData

data class RegisterPhoneDomainData(
    val phoneCodeData: RegisterData,
    val message: String,
    val status: String
)
