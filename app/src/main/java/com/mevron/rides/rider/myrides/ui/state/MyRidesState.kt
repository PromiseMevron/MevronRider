package com.mevron.rides.rider.myrides.ui.state

import com.mevron.rides.rider.myrides.domain.model.AllTripsResult
import com.mevron.rides.rider.savedplaces.domain.model.GetSavedAddressData

data class MyRidesState(
    val isLoading: Boolean,
    val isSuccess: Boolean,
    val error: String,
    val data: MutableList<AllTripsResult>,
    val openNextPage: Boolean,
    val backButton: Boolean
) {
    companion object {
        val EMPTY = MyRidesState(
            isLoading = false,
            isSuccess = false,
            error = "",
            data = mutableListOf(),
            openNextPage = false,
            backButton = false
        )
    }
}
