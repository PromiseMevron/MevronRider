package com.mevron.rides.rider.payment.ui

data class GetLinkState(
    val paymentLink: String,
    val error: String,
    val isLoading: Boolean,
    val amount: String,

) {
    companion object {
        val EMPTY = GetLinkState(
            paymentLink = "",
            error = "",
            isLoading = false,
            amount = "0"
        )
    }
}
