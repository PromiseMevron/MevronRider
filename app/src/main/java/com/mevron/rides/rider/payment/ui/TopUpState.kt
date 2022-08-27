package com.mevron.rides.rider.payment.ui

import com.mevron.rides.rider.payment.domain.PaymentBalanceDetailsDomainDatum
import com.mevron.rides.rider.payment.domain.PaymentCard

data class TopUpState(
    val loading: Boolean,
    val balance: String,
    val cashOutAmount: String,
    val addFundAmount: String,
    val cardNumber: String,
    val date: String,
    val errorMessage: String,
    val errorLink: String,
    val success: Boolean,
    val successCash: Boolean,
    val successFund: Boolean,
    val successCard: Boolean,
    val data: List<PaymentBalanceDetailsDomainDatum>,
    val cardData: List<PaymentCard>,
    val payLink: String,
    val addCard: Boolean,
    val shouldGoBack: Boolean,
    val confirmLink: String
){
    companion object {
        val EMPTY = TopUpState(
            loading = false,
            balance = "",
            errorMessage = "",
            success = false,
            data = mutableListOf(),
            date = "",
            cashOutAmount = "0",
            addFundAmount = "0",
            cardNumber = "",
            cardData = mutableListOf(),
            successCash = false,
            successFund = false,
            successCard = false,
            errorLink = "",
            payLink = "",
            addCard = false,
            shouldGoBack = false,
            confirmLink = ""
        )
    }
}