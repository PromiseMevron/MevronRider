package com.mevron.rides.rider.payment.ui

import com.mevron.rides.rider.payment.domain.PaymentCard

data class GetLinkSavedState(
    val paymentLink: String,
    val error: String,
    val isLoading: Boolean,
    val amount: String,
    val paymentCards:MutableList<PaymentCard>
) {
    companion object {
        val EMPTY = GetLinkSavedState(
            paymentLink = "",
            error = "",
            isLoading = false,
            amount = "0",
            paymentCards = mutableListOf()
        )
    }
}