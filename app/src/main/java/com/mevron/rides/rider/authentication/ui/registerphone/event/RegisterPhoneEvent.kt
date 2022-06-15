package com.mevron.rides.rider.authentication.ui.registerphone.event

sealed interface RegisterPhoneEvent{
    object NextButtonClick : RegisterPhoneEvent
}