package com.mevron.rides.rider.savedplaces.ui.saveaddress.event

sealed interface SaveAddressEvent{
    object OnTextChanged: SaveAddressEvent
    object OnBackButtonPressed: SaveAddressEvent
    object SaveHomeWorkAddress: SaveAddressEvent
    object SaveOtherAddress: SaveAddressEvent
    object AddressSelected: SaveAddressEvent
}