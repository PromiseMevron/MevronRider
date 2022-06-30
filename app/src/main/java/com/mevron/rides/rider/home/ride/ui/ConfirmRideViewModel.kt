package com.mevron.rides.rider.home.ride.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.TripState
import com.mevron.rides.rider.domain.usecase.GetTripStateUseCase
import com.mevron.rides.rider.home.booked.domain.TripStatus
import com.mevron.rides.rider.home.ride.domain.MakeRideRequestUseCase
import com.mevron.rides.rider.home.ride.model.ConfirmRideResponse
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.remote.HTTPErrorHandler
import com.mevron.rides.rider.remote.MevronRepo
import com.mevron.rides.rider.remote.model.RideRequest
import com.mevron.rides.rider.shared.ui.BaseViewModel
import com.mevron.rides.rider.sharedprefrence.domain.repository.IPreferenceRepository
import com.mevron.rides.rider.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.collect


@HiltViewModel
class ConfirmRideViewModel @Inject constructor(
    private val repository: MevronRepo,
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
                    if (tripState.data.metaData.status == TripStatus.ACCEPTED.status) {
                        setState { copy(isBookingConfirmed = true) }
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

    fun confirmRider(data: RideRequest): LiveData<GenericStatus<ConfirmRideResponse>> {

        val result = MutableLiveData<GenericStatus<ConfirmRideResponse>>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.createRide(data)
                if (response.isSuccessful) {
                    result.postValue(GenericStatus.Success(response.body()))
                } else {
                    result.postValue(
                        GenericStatus.Error(
                            HTTPErrorHandler.handleErrorWithCode(
                                response
                            )
                        )
                    )
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                result.postValue(GenericStatus.Error(HTTPErrorHandler.httpFailWithCode(ex)))
            }
        }
        return result
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