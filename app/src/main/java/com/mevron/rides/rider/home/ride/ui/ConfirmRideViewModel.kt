package com.mevron.rides.rider.home.ride.ui

import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.TripState
import com.mevron.rides.rider.domain.usecase.GetTripStateUseCase
import com.mevron.rides.rider.home.booked.domain.TripStatus
import com.mevron.rides.rider.home.ride.domain.MakeRideRequestUseCase
import com.mevron.rides.rider.remote.model.RideRequest
import com.mevron.rides.rider.shared.ui.BaseViewModel
import com.mevron.rides.rider.sharedprefrence.domain.repository.IPreferenceRepository
import com.mevron.rides.rider.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel
class ConfirmRideViewModel @Inject constructor(
    private val makeRideRequestUseCase: MakeRideRequestUseCase,
    private val preferenceRepo: IPreferenceRepository,
    private val getTripStateUseCase: GetTripStateUseCase
) : BaseViewModel<ConfirmRideState, ConfirmRideEvent>() {

    override fun createInitialState(): ConfirmRideState = ConfirmRideState.EMPTY

    private fun updateUUID() {
        setState {
            copy(uuid = preferenceRepo.getStringForKey(Constants.UUID))
        }
    }

    private fun observeSocketData() {
        viewModelScope.launch {
            getTripStateUseCase().collect { tripState ->

                if (tripState is TripState.TripStatusState) {
                    val tripStatus = tripState.data.metaData
                    if (tripStatus.status == TripStatus.ACCEPTED.status) {
                        setState {
                            copy(
                                isBookingConfirmed = true,
                                startLocationAddress = tripStatus.trip.pickupAddress,
                                destinationAddress = tripStatus.trip.destinationAddress,
                                startLocationLat = tripStatus.trip.pickupLatitude.toDouble(),
                                startLocationLng = tripStatus.trip.pickupLongitude.toDouble(),
                                endLocationLat = tripStatus.trip.destinationLatitude.toDouble(),
                                endLocationLng = tripStatus.trip.destinationLongitude.toDouble()
                            )
                        }
                    }
                }

                if (tripState is TripState.DriverSearchState) {
                    setState { copy(isSearchingDrivers = true) }
                }
            }
        }
    }

    private fun makeRideRequest() {
        setState { copy(isLoading = true) }

        val request = RideRequest(
            cardId = null,
            destinationAddress = uiState.value.destinationAddress,
            destinationLatitude = uiState.value.endLocationLat.toString(),
            destinationLongitude = uiState.value.endLocationLng.toString(),
            paymentMethod = uiState.value.paymentType.value,
            pickupAddress = uiState.value.destinationAddress,
            pickupLatitude = uiState.value.startLocationLat.toString(),
            pickupLongitude = uiState.value.startLocationLng.toString(),
            vehicleType = ""
        )

        viewModelScope.launch {
            val result = makeRideRequestUseCase(request)

            if (result is DomainModel.Success) {
                setState {
                    copy(
                        isLoading = false,
                        isRideConfirmed = true
                    )
                }

                // send Socket Event
            }
        }
    }

    fun resolveCoordinateRendered() {
        setState { copy(isCoordinateRendered = true) }
    }

    override fun setEvent(event: ConfirmRideEvent) {
        when (event) {
            ConfirmRideEvent.ConfirmRideRequest -> makeRideRequest()
            ConfirmRideEvent.CollectSocketData -> observeSocketData()
        }
    }

    init {
        updateUUID()
    }
}