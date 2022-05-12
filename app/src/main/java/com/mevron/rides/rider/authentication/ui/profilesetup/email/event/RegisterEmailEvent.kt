package com.mevron.rides.rider.authentication.ui.profilesetup.email.event

sealed interface RegisterEmailEvent{
    object NextButtonClick : RegisterEmailEvent
}