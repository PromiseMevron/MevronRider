package com.mevron.rides.rider.emerg.ui

sealed interface EmergencyEvent{
    object OpenNextPage: EmergencyEvent
    object MakeAPICall: EmergencyEvent
    object OnBackButtonClicked: EmergencyEvent
}