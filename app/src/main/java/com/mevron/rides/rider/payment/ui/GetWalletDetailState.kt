package com.mevron.rides.rider.payment.ui

import com.mevron.rides.rider.payment.domain.PaymentBalanceDetailsDomainDatum

data class GetWalletDetailState(
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
    val payLink: String
){
    companion object {
        val EMPTY = GetWalletDetailState(
            loading = false,
            balance = "",
            errorMessage = "",
            success = false,
            data = mutableListOf(),
            date = "",
            cashOutAmount = "0",
            addFundAmount = "0",
            cardNumber = "",
            successCash = false,
            successFund = false,
            successCard = false,
            errorLink = "",
            payLink = ""
        )
    }
}