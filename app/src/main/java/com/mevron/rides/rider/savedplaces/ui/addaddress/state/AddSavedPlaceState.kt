package com.mevron.rides.rider.savedplaces.ui.addaddress.state

import com.mevron.rides.rider.savedplaces.domain.model.GetSavedAddressData

data class AddSavedPlaceState(
    val isLoading: Boolean,
    val isSuccess: Boolean,
    val error: String,
    val data: MutableList<GetSavedAddressData>,
    val openNextPage: Boolean,
    val backButton: Boolean
) {
    companion object {
        val EMPTY = AddSavedPlaceState(
            isLoading = false,
            isSuccess = false,
            error = "",
            data = mutableListOf(),
            openNextPage = false,
            backButton = false
        )
    }
}
