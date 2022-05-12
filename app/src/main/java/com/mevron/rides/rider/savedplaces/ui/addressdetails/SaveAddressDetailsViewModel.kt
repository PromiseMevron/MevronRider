package com.mevron.rides.rider.savedplaces.ui.addressdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.savedplaces.data.model.SaveAddressRequest
import com.mevron.rides.rider.savedplaces.domain.usecase.SaveAddressUseCase
import com.mevron.rides.rider.savedplaces.ui.addressdetails.event.SaveAddressDetailsEvent
import com.mevron.rides.rider.savedplaces.ui.addressdetails.state.SaveAddressDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveAddressDetailsViewModel @Inject constructor(private val useCase: SaveAddressUseCase) :
    ViewModel() {

    private val mutableState: MutableStateFlow<SaveAddressDetailState> =
        MutableStateFlow(SaveAddressDetailState.EMPTY)
    val state: StateFlow<SaveAddressDetailState>
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
                        error = "Failure in saving details"
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

    private fun SaveAddressDetailState.buildRequest(): SaveAddressRequest =
        SaveAddressRequest(
            address = address,
            latitude = lat.toString(),
            longitude = lng.toString(),
            type = type,
            name = type
        )

    fun handleEvent(event: SaveAddressDetailsEvent) {
        when (event) {
            SaveAddressDetailsEvent.BackButtonPressed -> updateState(backButton = true)
            SaveAddressDetailsEvent.SaveAddressClicked -> saveAddress()
        }
    }

    fun updateState(
        isLoading: Boolean? = null,
        isSuccess: Boolean? = null,
        backButton: Boolean? = null,
        error: String? = null,
        lat: Double? = null,
        lng: Double? = null,
        address: String? = null,
        type: String? = null,
        name: String? = null
    ) {
        val currentState = mutableState.value
        val isCorrect = (name ?: currentState.name).isNotEmpty() && (address
            ?: currentState.address).isNotEmpty()
        mutableState.update {
            currentState.copy(
                isLoading = isLoading ?: currentState.isLoading,
                isSuccess = isSuccess ?: currentState.isSuccess,
                backButton = backButton ?: currentState.backButton,
                type = type ?: currentState.type,
                name = name ?: currentState.name,
                lat = lat ?: currentState.lat,
                lng = lng ?: currentState.lng,
                address = address ?: currentState.address,
                error = error ?: currentState.error,
                isCorrect = isCorrect
            )
        }
    }
}