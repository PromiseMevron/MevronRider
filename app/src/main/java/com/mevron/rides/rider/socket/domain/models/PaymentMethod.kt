package com.mevron.rides.rider.socket.domain.models

data class PaymentMethod(
    val id: String,
    val name: String,
    val type: String
)