package com.mevron.rides.rider.payment.domain

import com.google.gson.annotations.SerializedName

data class PaymentDetailsDomainData(
    val balance: String,
    @SerializedName("data")
    val data: List<PaymentDetailsDomainDatum>,
)