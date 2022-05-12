package com.mevron.rides.rider.authentication.ui.verifyotp.event

sealed interface VerifyOTPEvent{
    object OnOTPComplete : VerifyOTPEvent
}