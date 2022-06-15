package com.mevron.rides.rider.savedplaces.ui.addressdetails.event

sealed interface SaveAddressDetailsEvent{
    object BackButtonPressed: SaveAddressDetailsEvent
    object SaveAddressClicked: SaveAddressDetailsEvent
}