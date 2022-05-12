package com.mevron.rides.rider.savedplaces.ui.saveaddress.state

import com.google.android.libraries.places.api.model.AutocompletePrediction

data class SaveAddressState(
    val isLoading: Boolean,
    val isSuccess: Boolean,
    val backPressed: Boolean,
    val queryString: String,
    val queryChanged: Boolean,
    val autoCompletePredictions: MutableList<AutocompletePrediction>,
    val addressSClickedOn: Boolean,
    val type: String,
    val placeHolder: String,
    val title: String,
    val addressForEditText: String,
    val lat: Double,
    val lng: Double,
    val address: String,
    val error: String,
    val openNextPage: Boolean
){
    companion object {
        val EMPTY = SaveAddressState(
            isLoading = false,
            isSuccess = false,
            error = "",
            backPressed = false,
            queryString = "",
            autoCompletePredictions = mutableListOf(),
            addressSClickedOn = false,
            type = "",
            placeHolder = "",
            title = "",
            addressForEditText = "",
            address = "",
            lat = 0.0,
            lng = 0.0,
            openNextPage = false,
            queryChanged = false
        )
    }
}

