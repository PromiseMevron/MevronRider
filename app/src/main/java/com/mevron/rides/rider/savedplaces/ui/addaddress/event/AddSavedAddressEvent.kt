package com.mevron.rides.rider.savedplaces.ui.addaddress.event

sealed interface AddSavedAddressEvent{
    object OnAddNewClicked: AddSavedAddressEvent
    object GetNewAddress: AddSavedAddressEvent
    object OnBackButtonClicked: AddSavedAddressEvent
}