package com.mevron.rides.rider.emerg.ui.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.emerg.data.model.AddContactRequest
import com.mevron.rides.rider.emerg.data.model.Set
import com.mevron.rides.rider.emerg.domain.usecase.SaveContactUseCase
import com.mevron.rides.rider.emerg.ui.EmergencyEvent
import com.mevron.rides.rider.emerg.ui.EmergencyState
import com.mevron.rides.rider.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEmergencyViewModel @Inject constructor(private val useCase: SaveContactUseCase) :
    ViewModel() {

    private val mutableState: MutableStateFlow<EmergencyState> =
        MutableStateFlow(EmergencyState.EMPTY)

    val state: StateFlow<EmergencyState>
        get() = mutableState

    private fun saveEmergency() {
        updateState(isLoading = true)
        val data = mutableState.value.buildRequest()
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = useCase(data)) {
                is DomainModel.Error -> mutableState.update {
                    mutableState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = result.error.localizedMessage ?: Constants.UNEXPECTED_ERROR
                    )
                }
                is DomainModel.Success -> {
                    updateState(
                        isLoading = false,
                        isRequestSuccess = false
                    )
                }
            }
        }
    }

    fun handleEvent(event: EmergencyEvent) {
        when (event) {
            is EmergencyEvent.MakeAPICall ->
                saveEmergency()
            is EmergencyEvent.OnBackButtonClicked -> updateState(backButton = true)
            is EmergencyEvent.OpenNextPage -> updateState(updateAddress = true)
        }
    }

    fun updateState(
        isLoading: Boolean? = null,
        isRequestSuccess: Boolean? = null,
        savedAddresses: MutableList<Set>? = null,
        updateAddress: Boolean? = null,
        backButton: Boolean? = null,
        error: String? = null,
    ) {
        val currentState = mutableState.value
        mutableState.update {
            currentState.copy(
                isLoading = isLoading ?: currentState.isLoading,
                isSuccessAdd = isRequestSuccess ?: currentState.isSuccess,
                data = savedAddresses ?: currentState.data,
                openNextPage = updateAddress ?: currentState.openNextPage,
                backButton = backButton ?: currentState.backButton,
                error = error ?: currentState.error
            )
        }
    }

    private fun EmergencyState.buildRequest(): AddContactRequest {
        return AddContactRequest(sets = this.data)
    }
}