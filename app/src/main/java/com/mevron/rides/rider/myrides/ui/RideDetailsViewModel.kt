package com.mevron.rides.rider.myrides.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.myrides.domain.model.AllTripsResult
import com.mevron.rides.rider.myrides.domain.model.TripDetailDomainData
import com.mevron.rides.rider.myrides.domain.usecases.GetARideDetails
import com.mevron.rides.rider.myrides.ui.event.MyRidesEvents
import com.mevron.rides.rider.myrides.ui.state.MyRidesState
import com.mevron.rides.rider.payment.domain.PaymentDetailsDomainData
import com.mevron.rides.rider.savedplaces.domain.model.GetSavedAddressData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RideDetailsViewModel  @Inject constructor(private val useCase: GetARideDetails): ViewModel() {

    private val mutableState: MutableStateFlow<MyRidesState> =
        MutableStateFlow(MyRidesState.EMPTY)

    val state: StateFlow<MyRidesState>
        get() = mutableState

    fun handleEvent(event: MyRidesEvents) {
        when (event) {
            is MyRidesEvents.GetAddress ->
                getDetails()
            is MyRidesEvents.OnBackPressed -> updateState(backButton = true)
            MyRidesEvents.OpenNewDetails -> {

            }
        }
    }

    private fun getDetails() {
        updateState(isLoading = true)
        val id = mutableState.value.rideId
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = useCase(id)) {
                is DomainModel.Success -> {
                    val data = result.data as TripDetailDomainData
                    updateState(
                        isLoading = false,
                        errorMessage = "",
                        rideDetail = data,
                    )
                }
                is DomainModel.Error -> mutableState.update {
                    mutableState.value.copy(
                        isLoading = false,
                        error = "Failure to get your ride details",
                    )
                }
            }
        }
    }

    fun updateState(
        isLoading: Boolean? = null,
        isRequestSuccess: Boolean? = null,
        savedAddresses: MutableList<AllTripsResult>? = null,
        rideDetail: TripDetailDomainData? = null,
        updateAddress: Boolean? = null,
        backButton: Boolean? = null,
        rideId: String? = null,
        errorMessage: String? = null
    ) {
        val currentState = mutableState.value
        mutableState.update {
            currentState.copy(
                isLoading = isLoading ?: currentState.isLoading,
                isSuccess = isRequestSuccess ?: currentState.isSuccess,
                data = savedAddresses ?: currentState.data,
                openNextPage = updateAddress ?: currentState.openNextPage,
                backButton = backButton ?: currentState.backButton,
                rideDetail = rideDetail ?: currentState.rideDetail,
                rideId = rideId ?: currentState.rideId,
                error = errorMessage ?: currentState.error
            )
        }
    }
}