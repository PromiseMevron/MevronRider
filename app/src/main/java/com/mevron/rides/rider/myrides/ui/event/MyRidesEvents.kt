package com.mevron.rides.rider.myrides.ui.event

sealed interface MyRidesEvents {
    object OpenNewDetails : MyRidesEvents
    object GetAddress : MyRidesEvents
    object OnBackPressed : MyRidesEvents
}