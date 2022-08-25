package com.mevron.rides.rider.payment.domain

data class PaymentBalanceDetailsDomainDatum(
    val date: String,
    val data: List<PaymentDetailsDomainDatum>
)


data class PaymentDetailsDomainDatum(
    val amount: String,
    val date: String,
    val icon: String,
    val method: String,
    val narration: String,
    val time: String
)