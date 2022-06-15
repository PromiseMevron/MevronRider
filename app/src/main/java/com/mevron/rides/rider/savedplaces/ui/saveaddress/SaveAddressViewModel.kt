package com.mevron.rides.rider.savedplaces.ui.saveaddress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.savedplaces.data.model.SaveAddressRequest
import com.mevron.rides.rider.savedplaces.domain.usecase.SaveAddressUseCase
import com.mevron.rides.rider.savedplaces.ui.saveaddress.event.SaveAddressEvent
import com.mevron.rides.rider.savedplaces.ui.saveaddress.state.SaveAddressState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveAddressViewModel @Inject constructor(private val useCase: SaveAddressUseCase) :
    ViewModel() {

    private val mutableState: MutableStateFlow<SaveAddressState> =
        MutableStateFlow(SaveAddressState.EMPTY)
    val state: StateFlow<SaveAddressState>
        get() = mutableState

    private fun saveAddress() {
        updateState(isLoading = true)
        val data = mutableState.value.buildRequest()
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = useCase(data)) {
                is DomainModel.Error -> mutableState.update {
                    mutableState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = ""
                    )
                }
                is DomainModel.Success -> {
                    updateState(
                        isLoading = false,
                        isSuccess = true
                    )
                }
            }
        }
    }

    private fun SaveAddressState.buildRequest(): SaveAddressRequest =
        SaveAddressRequest(
            address = address,
            latitude = lat.toString(),
            longitude = lng.toString(),
            type = type,
            name = type
        )

    fun handleEvent(event: SaveAddressEvent) {
        when (event) {
            is SaveAddressEvent.AddressSelected ->
                updateState(addressSClickedOn = true)
            is SaveAddressEvent.OnBackButtonPressed ->
                updateState(backPressed = true)
            is SaveAddressEvent.OnTextChanged ->
                updateState(queryChanged = true)
            is SaveAddressEvent.SaveHomeWorkAddress ->
                saveAddress()
            is SaveAddressEvent.SaveOtherAddress ->
                updateState(openNextPage = true)
        }
    }

    fun processEventLocation(prediction: AutocompletePrediction, placesClient: PlacesClient) {
        val placeFilters =
            listOf(Place.Field.NAME, Place.Field.ID, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val type = mutableState.value.type
        val fetchRequest = FetchPlaceRequest.builder(prediction.placeId, placeFilters).build()
        // TODO: Add the user device country default to search parameter
        placesClient.fetchPlace(fetchRequest)
            .addOnSuccessListener {
                val coordinates = it.place.latLng!!
                updateState(
                    lat = coordinates.latitude,
                    lng = coordinates.longitude,
                    address = it.place.address!!
                )
                val address: String = if (it.place.name == it.place.address) {
                    it.place.name ?: ""
                } else {
                    it.place.name ?: "" + ", " + it.place.address
                }
                updateState(addressForEditText = address)
                if (type == "others") {
                    if (it != null) {
                        handleEvent(SaveAddressEvent.SaveOtherAddress)
                    }
                } else {
                    handleEvent(SaveAddressEvent.SaveHomeWorkAddress)
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun initSearchConfig(sessionToken: AutocompleteSessionToken, placesClient: PlacesClient) {
        val query = mutableState.value.queryString
        val placeRequest = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(sessionToken)
            .setQuery(query)
            .build()
        placesClient.findAutocompletePredictions(placeRequest)
            .addOnSuccessListener {
                val response = it
                if (response.autocompletePredictions.isNotEmpty()) {
                    updateState(autoCompletePredictions = response.autocompletePredictions)
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun updateState(
        isLoading: Boolean? = null,
        backPressed: Boolean? = null,
        isSuccess: Boolean? = null,
        queryString: String? = null,
        autoCompletePredictions: MutableList<AutocompletePrediction>? = null,
        addressSClickedOn: Boolean? = null,
        type: String? = null,
        placeHolder: String? = null,
        title: String? = null,
        addressForEditText: String? = null,
        lat: Double? = null,
        lng: Double? = null,
        address: String? = null,
        error: String? = null,
        openNextPage: Boolean? = null,
        queryChanged: Boolean? = null
    ) {
        val currentState = mutableState.value
        mutableState.update {
            currentState.copy(
                isLoading = isLoading ?: currentState.isLoading,
                isSuccess = isSuccess ?: currentState.isSuccess,
                backPressed = backPressed ?: currentState.backPressed,
                queryString = queryString ?: currentState.queryString,
                autoCompletePredictions = autoCompletePredictions
                    ?: currentState.autoCompletePredictions,
                addressSClickedOn = addressSClickedOn ?: currentState.addressSClickedOn,
                type = type ?: currentState.type,
                placeHolder = placeHolder ?: currentState.placeHolder,
                title = title ?: currentState.title,
                addressForEditText = addressForEditText ?: currentState.addressForEditText,
                lat = lat ?: currentState.lat,
                lng = lng ?: currentState.lng,
                address = address ?: currentState.address,
                error = error ?: currentState.error,
                openNextPage = openNextPage ?: currentState.openNextPage,
                queryChanged = queryChanged ?: currentState.queryChanged
            )
        }
    }
}