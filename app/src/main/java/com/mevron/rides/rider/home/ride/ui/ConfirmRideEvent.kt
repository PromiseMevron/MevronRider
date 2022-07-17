package com.mevron.rides.rider.home.ride.ui

sealed interface ConfirmRideEvent {
    object ConfirmRideRequest: ConfirmRideEvent
    object CollectSocketData : ConfirmRideEvent
}