package com.mevron.rides.rider.myrides.ui.event

sealed interface GetAddressEvents {
    object OpenNewDetails : GetAddressEvents
    object GetAddress : GetAddressEvents
    object OnBackPressed : GetAddressEvents
}