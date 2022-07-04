package com.mevron.rides.rider.payment.domain

data class PaymentCard(
    val bin: String,
    val brand: String,
    val expiryMonth: String,
    val expiryYear: String,
    val lastDigits: String,
    val type: String,
    val uuid: String
)

data class PaymentCardDomainModel(
    val cards: List<PaymentCard> = listOf()
)