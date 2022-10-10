package com.mevron.rides.rider.home.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.TripState
import com.mevron.rides.rider.domain.usecase.GetTripStateUseCase
import com.mevron.rides.rider.domain.usecase.SetOrderPropertiesUseCase
import com.mevron.rides.rider.home.booked.domain.TripStatus
import com.mevron.rides.rider.home.booked.domain.toTripStatus
import com.mevron.rides.rider.home.data.DeviceID
import com.mevron.rides.rider.home.domain.FCMTokenUseCase
import com.mevron.rides.rider.home.domain.GetProfileUseCase
import com.mevron.rides.rider.home.domain.ProfileDomainData
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.savedplaces.domain.model.GetAddressDomainData
import com.mevron.rides.rider.savedplaces.domain.usecase.GetAddressUseCase
import com.mevron.rides.rider.shared.ui.BaseViewModel
import com.mevron.rides.rider.shared.ui.SingleStateEvent
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.Constants.SUPPORT_NUMBER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAddressUseCase: GetAddressUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val tripStateUseCase: GetTripStateUseCase,
    private val setPreferenceUseCase: SetOrderPropertiesUseCase,
    private val fcmTokenUseCase: FCMTokenUseCase
) : BaseViewModel<HomeState, HomeEvent>() {

    override fun createInitialState(): HomeState = HomeState.EMPTY

    private fun getAddresses() {
        setPreferenceUseCase(Constants.HOME, "")
        setPreferenceUseCase(Constants.WORK, "")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                setState { copy(isLoading = true, error = "") }
                val networkData = getAddressUseCase()
                if (networkData is DomainModel.Success) {
                    val data = (networkData.data as GetAddressDomainData).savedAddresses
                    for (d in data) {
                        if (d.type == Constants.HOME) {
                            setPreferenceUseCase(Constants.HOME, d.address)
                        }
                        if (d.type == Constants.WORK) {
                            setPreferenceUseCase(Constants.WORK, d.address)
                        }
                    }
                    setState {
                        copy(
                            savedAddresses = data,
                            error = "", isLoading = false
                        )
                    }
                } else {
                    setState {
                        copy(
                            error = (networkData as DomainModel.Error).error.toString(),
                            isLoading = false
                        )
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                setState { copy(error = ex.toString(), isLoading = false) }
            }
        }
    }

    private fun openSearch() {
        setState { copy(isOpenSearchClicked = true) }
    }

    override fun setEvent(event: HomeEvent) = when (event) {
        HomeEvent.LoadAddresses -> getAddresses()
        HomeEvent.OnSearchClicked -> openSearch()
        HomeEvent.LoadProfile -> getProfile()
        HomeEvent.OnSearchOpened -> setState { copy(isOpenSearchClicked = false) }
        HomeEvent.OnAllSavedAddressClicked -> setState { copy(isAddSavePlaceClicked = true) }
        HomeEvent.ScheduleButtonClicked -> setState {
            copy(
                isScheduleClicked = true,
                isScheduleTheRideClicked = false
            )
        }
        HomeEvent.ScheduleTheRideClicked -> setState {
            copy(
                isScheduleClicked = false,
                isScheduleTheRideClicked = true
            )
        }
        HomeEvent.ObserveTripState -> loadTripState()
    }

    private fun loadTripState() {
        viewModelScope.launch {
            tripStateUseCase().collect { tripState ->
                if (tripState is TripState.NearByDriversState) {
               //        setState { copy(markerLocations = tripState.data.locations) }
                }
                when (tripState) {
                    is TripState.DriverSearchState -> {
                        setState { copy(hideStateCheckCover = true) }
                        setState { copy(shouldOpenConfirmRide = true) }
                    }

                    is TripState.StateMachineState -> {
                        setState { copy(hideStateCheckCover = true) }
                        val currentStatus = tripState.data.meta_data.status.toTripStatus()
                        if (currentStatus != TripStatus.UNKNOWN && currentStatus != TripStatus.COMPLETED) {
                            setState { copy(shouldOpenBookedRide = true) }
                        }
                    }

                    is TripState.TripStatusState -> {
                        setState { copy(hideStateCheckCover = true) }
                        if (tripState.data.metaData.status.toTripStatus() == TripStatus.TRIP_COMPLETED) {
                            setState {
                                copy(shouldOpenTipView = SingleStateEvent<Unit>().apply {
                                    set(
                                        Unit
                                    )
                                })
                            }
                        }
                        // setState { copy(shouldOpenBookedRide = true) }
                    }

                    else -> {
                        setState { copy(hideStateCheckCover = true) }
                    }
                }
            }
        }
    }

    private fun getProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                setState { copy(isLoading = true, error = "") }
                val profileResponse = getProfileUseCase()

                if (profileResponse is DomainModel.Success) {
                    setPreferenceUseCase(SUPPORT_NUMBER, (profileResponse.data as ProfileDomainData).supportNumber ?: "")
                    setState {
                        copy(
                            profileData = profileResponse.data as ProfileDomainData,
                            isLoading = false
                        )
                    }
                } else {
                    setState { copy(error = (profileResponse as DomainModel.Error).error.toString()) }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                setState { copy(isLoading = false, error = ex.toString()) }
            }
        }
    }

    fun updateToken(id: String) {
        setState {
            copy(
                deviceID = id
            )
        }
        if (uiState.value.deviceID.isEmpty()) {
            setState {
                copy(
                    tokenSuccessful = true
                )
            }
            return
        }
        val theId = uiState.value.deviceID
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = fcmTokenUseCase(DeviceID(device_id = theId))

                if (result is DomainModel.Success) {
                    setState {
                        copy(
                            tokenSuccessful = true
                        )
                    }
                } else {
                    setState {
                        copy(
                            tokenSuccessful = true
                        )
                    }
                }
            } catch (ex: Exception) {
                setState {
                    copy(
                        tokenSuccessful = true
                    )
                }
            }
        }
    }

    fun updateOrderStatus(model: List<LocationModel>) {
        Log.d("DIRECTION", model[0].lat.toString())
        setPreferenceUseCase(Constants.PICK_UP_ADD, model[0].address)
        setPreferenceUseCase(Constants.DROP_OFF_ADD, model[1].address)
        setPreferenceUseCase(Constants.PICK_UP_LAT, model[0].lat.toString())
        setPreferenceUseCase(Constants.DROP_OFF_LAT, model[1].lat.toString())
        setPreferenceUseCase(Constants.PICK_UP_LNG, model[0].lng.toString())
        setPreferenceUseCase(Constants.DROP_OFF_LNG, model[1].lng.toString())
    }

    fun resolveConfirmRide() {
        setState { copy(shouldOpenConfirmRide = false) }
    }

    fun resolveOpenBookedRide() {
        setState { copy(shouldOpenBookedRide = false) }
    }

    fun resolveSearchClicked() {
        setState { copy(isOpenSearchClicked = false) }
    }

    fun resolveAddSavePlaceClicked() {
        setState { copy(isAddSavePlaceClicked = false) }
    }

    init {
        setEvent(HomeEvent.LoadAddresses)
    }
}