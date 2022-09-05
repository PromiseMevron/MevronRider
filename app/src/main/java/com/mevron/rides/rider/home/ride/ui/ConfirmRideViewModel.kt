package com.mevron.rides.rider.home.ride.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.IOpenBookedStateRepository
import com.mevron.rides.rider.domain.TripState
import com.mevron.rides.rider.domain.usecase.GetOpenBookUseCase
import com.mevron.rides.rider.domain.usecase.GetOrderPropertiesUseCase
import com.mevron.rides.rider.domain.usecase.GetTripStateUseCase
import com.mevron.rides.rider.domain.usecase.SetOrderPropertiesUseCase
import com.mevron.rides.rider.home.booked.domain.TripStatus
import com.mevron.rides.rider.home.ride.domain.CancelRideRequestUseCase
import com.mevron.rides.rider.home.ride.domain.MakeRideRequestUseCase
import com.mevron.rides.rider.home.ride.domain.RideDomainData
import com.mevron.rides.rider.remote.model.CancelRideRequest
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
    private val getTripStateUseCase: GetTripStateUseCase,
    private val openBookedUseCase: GetOpenBookUseCase,
    private val getOrderPropertiesUseCase: GetOrderPropertiesUseCase,
    private val openBookedStateRepository: IOpenBookedStateRepository,
    private val cancelRideRequestUseCase: CancelRideRequestUseCase,
    private val setPrefrence: SetOrderPropertiesUseCase
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

               /* if (tripState is TripState.TripStatusState) {
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
                }*/

                if (tripState is TripState.RideAccepted) {
                    Log.d("ssss", "wewewe 11")
                    setState { copy(isBookingConfirmed = true) }
                }

                if (tripState is TripState.StateMachineState) {
                    if (tripState.data != null){
                        if (tripState.data.meta_data != null){
                            Log.d("ssss", "wewewe 12 ${tripState.data.meta_data.status}")
                            if (tripState.data.meta_data.status == "accepted")
                                setState { copy(isBookingConfirmed = true) }
                        }

                    }

                }

             /*   if (tripState is TripState.DriverSearchState) {
                  //  setState { copy(isSearchingDrivers = true) }
                }*/
            }
            openBookedUseCase().collect {
                Log.d("ssss", "wewewe")
                setState { copy(isBookingConfirmed = it) }
            }
        }
    }

    fun updateOpenBooked() {
       // openBookedStateRepository.setTripState(false)
    }

    fun updateOrderParamStatus() {
        setState {
            copy(
                startLocationAddress = getOrderPropertiesUseCase(Constants.PICK_UP_ADD),
                destinationAddress = getOrderPropertiesUseCase(Constants.DROP_OFF_ADD),
                startLocationLat = getOrderPropertiesUseCase(Constants.PICK_UP_LAT).toDouble(),
                startLocationLng = getOrderPropertiesUseCase(Constants.PICK_UP_LNG).toDouble(),
                endLocationLat = getOrderPropertiesUseCase(Constants.DROP_OFF_LAT).toDouble(),
                endLocationLng = getOrderPropertiesUseCase(Constants.DROP_OFF_LNG).toDouble(),
                payId = getOrderPropertiesUseCase(Constants.ThePaymentModel),
                vehicleId = getOrderPropertiesUseCase(Constants.CAR)
            )
        }
    }

    private fun makeRideRequest() {
       // setState { copy(isLoading = true) }

       /* val request = RideRequest(
            cardId = null,
            destinationAddress = uiState.value.destinationAddress,
            destinationLatitude = uiState.value.endLocationLat.toString(),
            destinationLongitude = uiState.value.endLocationLng.toString(),
            paymentMethod = uiState.value.payId,
            pickupAddress = uiState.value.startLocationAddress,
            pickupLatitude = uiState.value.startLocationLat.toString(),
            pickupLongitude = uiState.value.startLocationLng.toString(),
            vehicleType = uiState.value.vehicleId
        )*/

        val request = RideRequest(
            cardId = null,
            destinationAddress = "Qwerty",
            destinationLatitude = "6.570466",
            destinationLongitude = "3.367385",
            paymentMethod = uiState.value.payId,
            pickupAddress = "qwety road",
            pickupLatitude = "6.408059",
            pickupLongitude = "3.239489",
            vehicleType = uiState.value.vehicleId
        )

        viewModelScope.launch {
            val result = makeRideRequestUseCase(request)

            if (result is DomainModel.Success) {
                val dt = result.data as RideDomainData
                setState {
                    setPrefrence(Constants.TRIP_ID, dt.uuid)
                    copy(
                        trip_Id = dt.uuid,
                        isLoading = false,
                        isRideConfirmed = true
                    )
                }

                // send Socket Event
            }
        }
    }

    fun cancelARide(){
        setState {
            copy(
                isLoading = true,
                isRideCancelled = false
            )
        }
        val request = CancelRideRequest(trip_id = uiState.value.trip_Id, comment = uiState.value.reasonForCancel)
        viewModelScope.launch {
            val result = cancelRideRequestUseCase(request)

            if (result is DomainModel.Success) {
                setState {
                    copy(
                        isLoading = false,
                        isRideCancelled = true
                    )
                }
            }else{
                setState {
                    copy(
                        isLoading = false,
                        isRideCancelled = false
                    )
                }
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

    fun updateCancelValue(value: String) {
        setState {
            copy(reasonForCancel = value)
        }
    }

    init {
        updateUUID()
    }
}