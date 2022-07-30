package com.mevron.rides.rider.socket.domain.models

data class MetaData(
    val driver: Driver,
    val fare: List<Fare>,
    val paymentMethod: PaymentMethod,
    val rider: Rider,
    val status: String,
    val trip: Trip
)