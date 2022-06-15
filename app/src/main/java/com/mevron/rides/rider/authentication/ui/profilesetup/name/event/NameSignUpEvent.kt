package com.mevron.rides.rider.authentication.ui.profilesetup.name.event

sealed interface NameSignUpEvent{
    object OnBottonClicked : NameSignUpEvent
}