package com.mevron.rides.rider.payment.data

import com.google.gson.annotations.SerializedName

data class PaymentDetailsResponse(
    @SerializedName("success")
    val paySuccess: PaymentDetailsSuccess
)

data class PaymentDetailsSuccess(
    @SerializedName("data")
    val payData: PayData,
    val message: String,
    val status: String
)

data class PayData(
    val balance: String,
    val currency: String,
    val transactions: List<PayDataDatum>
)

data class PayDataDatum(
    val amount: String,
    val date: String,
    val icon: String,
    val method: String,
    val narration: String,
    val time: String
)