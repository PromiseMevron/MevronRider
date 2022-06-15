package com.mevron.rides.rider.myrides.ui

import androidx.lifecycle.ViewModel
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.myrides.domain.usecases.GetAllRideUseCase
import com.mevron.rides.rider.myrides.ui.event.MyRidesEvents
import com.mevron.rides.rider.myrides.ui.state.MyRidesState
import com.mevron.rides.rider.savedplaces.domain.model.GetSavedAddressData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class MyCompletedRidesViewModel @Inject constructor(private val useCase: GetAllRideUseCase): ViewModel() {

    private val mutableState: MutableStateFlow<MyRidesState> =
        MutableStateFlow(MyRidesState.EMPTY)

    val state: StateFlow<MyRidesState>
        get() = mutableState

    fun handleEvent(event: MyRidesEvents) {
        when (event) {
            is MyRidesEvents.GetAddress ->
                getDetails()
            is MyRidesEvents.OnBackPressed -> updateState(backButton = true)
            is MyRidesEvents.OpenNewDetails -> updateState(updateAddress = true)
        }
    }

    private fun getDetails() {

    }

    fun updateState(
        isLoading: Boolean? = null,
        isRequestSuccess: Boolean? = null,
        savedAddresses: MutableList<GetSavedAddressData>? = null,
        updateAddress: Boolean? = null,
        backButton: Boolean? = null
    ) {
        val currentState = mutableState.value
        mutableState.update {
            currentState.copy(
                isLoading = isLoading ?: currentState.isLoading,
                isSuccess = isRequestSuccess ?: currentState.isSuccess,
                data = savedAddresses ?: currentState.data,
                openNextPage = updateAddress ?: currentState.openNextPage,
                backButton = backButton ?: currentState.backButton
            )
        }
    }
}