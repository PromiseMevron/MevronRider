package com.mevron.rides.rider.myrides.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.myrides.domain.model.AllTripsResult
import com.mevron.rides.rider.myrides.domain.model.GetAllTripsDomainData
import com.mevron.rides.rider.myrides.domain.usecases.GetAllRideUseCase
import com.mevron.rides.rider.myrides.ui.event.MyRidesEvents
import com.mevron.rides.rider.myrides.ui.state.MyRidesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCompletedRidesViewModel @Inject constructor(private val useCase: GetAllRideUseCase) :
    ViewModel() {

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
                    val data = result.data as GetAllTripsDomainData
                    updateState(
                        isLoading = false,
                        isRequestSuccess = true,
                        data = data.data.toMutableList()
                    )
                }
            }
        }
    }

    fun updateState(
        isLoading: Boolean? = null,
        isRequestSuccess: Boolean? = null,
        data: MutableList<AllTripsResult>? = null,
        updateAddress: Boolean? = null,
        backButton: Boolean? = null
    ) {
        val currentState = mutableState.value
        mutableState.update {
            currentState.copy(
                isLoading = isLoading ?: currentState.isLoading,
                isSuccess = isRequestSuccess ?: currentState.isSuccess,
                data = data ?: currentState.data,
                openNextPage = updateAddress ?: currentState.openNextPage,
                backButton = backButton ?: currentState.backButton
            )
        }
    }
}