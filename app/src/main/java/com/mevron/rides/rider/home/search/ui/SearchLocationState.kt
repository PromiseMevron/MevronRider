package com.mevron.rides.rider.home.search.ui

class SearchLocationState(
    val isCloseClicked: Boolean,
    val origin: String,
    val destination: String,
    val isAddHomeClicked: Boolean,
    val isAddWorkClicked: Boolean,
    val isCurrentLocationClicked: Boolean,
    val isSetLocationOnTheMapClicked: Boolean
) {

    val isAddressEntered: Boolean
        get() = origin.isNotEmpty() && destination.isNotEmpty()
}

sealed interface SearchLocationEvent {
    object AddHomeClicked: SearchLocationEvent
    object AddWorkClicked: SearchLocationEvent
    object CurrentLocationClicked: SearchLocationEvent
    object SetLocationOnTheMapClicked: SearchLocationEvent
}