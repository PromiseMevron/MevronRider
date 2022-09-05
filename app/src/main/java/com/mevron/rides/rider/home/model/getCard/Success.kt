package com.mevron.rides.rider.home.model.getCard

data class Success(
    val `data`: WalletData,
    val message: String,
    val status: String
)

data class WalletData(
    val card: List<Data>,
    val balance: String,
)