package com.mevron.rides.rider.payment.ui



sealed interface WalletEvent{
    object GetWalletDetail: WalletEvent
}