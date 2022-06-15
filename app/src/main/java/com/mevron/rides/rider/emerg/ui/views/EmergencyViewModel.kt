package com.mevron.rides.rider.emerg.ui.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.emerg.data.model.Set
import com.mevron.rides.rider.emerg.domain.model.GetContactDomain
import com.mevron.rides.rider.emerg.domain.model.GetContactDomainData
import com.mevron.rides.rider.emerg.domain.usecase.GetContactUseCase
import com.mevron.rides.rider.emerg.ui.EmergencyEvent
import com.mevron.rides.rider.emerg.ui.EmergencyState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmergencyViewModel @Inject constructor(private val useCase: GetContactUseCase) : ViewModel() {

    private val mutableState: MutableStateFlow<EmergencyState> =
        MutableStateFlow(EmergencyState.EMPTY)

    val state: StateFlow<EmergencyState>
        get() = mutableState

    private fun getEmergency() {
        updateState(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = useCase()) {
                is DomainModel.Error -> mutableState.update {
                    mutableState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = ""
                    )
                }
                is DomainModel.Success -> {
                    val data = result.data as GetContactDomain
                    updateState(
                        isLoading = false,
                        isRequestSuccess = false,
                        result = data.savedAddresses.toMutableList()
                    )
                }
            }
        }
    }

    fun handleEvent(event: EmergencyEvent) {
        when (event) {
            is EmergencyEvent.MakeAPICall ->
                getEmergency()
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
        result: MutableList<GetContactDomainData>? = null
    ) {
        val currentState = mutableState.value
        mutableState.update {
            currentState.copy(
                isLoading = isLoading ?: currentState.isLoading,
                isSuccess = isRequestSuccess ?: currentState.isSuccess,
                data = savedAddresses ?: currentState.data,
                openNextPage = updateAddress ?: currentState.openNextPage,
                backButton = backButton ?: currentState.backButton,
                result = result ?: currentState.result
            )
        }
    }
}