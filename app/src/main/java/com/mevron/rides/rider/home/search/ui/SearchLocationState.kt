package com.mevron.rides.rider.home.search.ui

import com.mevron.rides.rider.savedplaces.domain.model.GetSavedAddressData

data class SearchLocationState(
    val isLoading: Boolean,
    val isCloseClicked: Boolean,
    val origin: String,
    val destination: String,
    val isAddHomeClicked: Boolean,
    val isAddWorkClicked: Boolean,
    val isCurrentLocationClicked: Boolean,
    val isSetLocationOnTheMapClicked: Boolean,
    val savedAddresses: List<GetSavedAddressData>,
    val error: String,
    val defaultCountry: String
) {
    val isAddressEntered: Boolean
        get() = origin.isNotEmpty() && destination.isNotEmpty()

    companion object {
        val EMPTY = SearchLocationState(
            isLoading = false,
            isCloseClicked = false,
            origin = "",
            destination = "",
            isAddHomeClicked = false,
            isAddWorkClicked = false,
            isCurrentLocationClicked = false,
            isSetLocationOnTheMapClicked = false,
            savedAddresses = listOf(),
            error = "",
            defaultCountry = ""
        )
    }
}

sealed interface SearchLocationEvent {
    object AddHomeClicked : SearchLocationEvent
    object AddWorkClicked : SearchLocationEvent
    object CurrentLocationClicked : SearchLocationEvent
    object SetLocationOnTheMapClicked : SearchLocationEvent
    object GetAddress : SearchLocationEvent
}