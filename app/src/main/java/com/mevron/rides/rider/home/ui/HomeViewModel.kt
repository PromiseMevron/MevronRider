package com.mevron.rides.rider.home.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.TripState
import com.mevron.rides.rider.domain.usecase.GetTripStateUseCase
import com.mevron.rides.rider.home.booked.domain.TripStatus
import com.mevron.rides.rider.home.booked.domain.toTripStatus
import com.mevron.rides.rider.home.domain.GetProfileUseCase
import com.mevron.rides.rider.home.domain.ProfileDomainData
import com.mevron.rides.rider.savedplaces.domain.model.GetAddressDomainData
import com.mevron.rides.rider.savedplaces.domain.usecase.GetAddressUseCase
import com.mevron.rides.rider.shared.ui.BaseViewModel
import com.mevron.rides.rider.shared.ui.SingleStateEvent
import com.mevron.rides.rider.sharedprefrence.domain.usescases.SetPreferenceUseCase
import com.mevron.rides.rider.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAddressUseCase: GetAddressUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val tripStateUseCase: GetTripStateUseCase,
    private val setPreferenceUseCase: SetPreferenceUseCase
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
                    for (d in data){
                        if (d.type == Constants.HOME){
                            setPreferenceUseCase(Constants.HOME, d.address)
                        }
                        if (d.type == Constants.WORK){
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
                Log.d("sdsdd", "sdsdss 5 $tripState")
                if (tripState is TripState.NearByDriversState) {
                 //   setState { copy(markerLocations = tripState.data.locations) }
                }
                when (tripState) {
                    is TripState.DriverSearchState -> {
                        setState { copy(shouldOpenConfirmRide = true) }
                    }

                    is TripState.StateMachineState -> {
                        setState { copy(hideStateCheckCover = true) }
                        Log.d("sdsdd", "sdsdss")
                       val currentStatus = tripState.data.meta_data.status.toTripStatus()
                        Log.d("sdsdd", "sdsdss 595959m $currentStatus")
                        if (currentStatus != TripStatus.UNKNOWN && currentStatus != TripStatus.COMPLETED){
                            Log.d("sdsdd", "sdsdss 444")
                            setState { copy(shouldOpenBookedRide = true) }
                        }
                    }

                    is TripState.TripStatusState -> {
                        if (tripState.data.metaData.status.toTripStatus() == TripStatus.TRIP_COMPLETED) {
                            setState { copy(shouldOpenTipView = SingleStateEvent<Unit>().apply { set(Unit) }) }
                        }
                       // setState { copy(shouldOpenBookedRide = true) }
                    }

                    else -> {}
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