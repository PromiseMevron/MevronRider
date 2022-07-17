package com.mevron.rides.rider.home.ui

import com.mevron.rides.rider.home.domain.ProfileDomainData
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.savedplaces.domain.model.GetSavedAddressData
import com.mevron.rides.rider.shared.ui.SingleStateEvent
import com.mevron.rides.rider.socket.domain.models.NearByDriver

data class HomeState(
    val startLocation: String,
    val destinationLocation: String,
    val savedAddresses: List<GetSavedAddressData>,
    val profileData: ProfileDomainData,
    val isOpenSearchClicked: Boolean,
    val error: String,
    val isScheduleClicked: Boolean,
    val isAddSavePlaceClicked: Boolean,
    val isLoading: Boolean,
    val isScheduleTheRideClicked: Boolean,
    val isMyLocationButtonClicked: Boolean,
    val locationModel: LocationModel,
    val isLocationAdded: Boolean,
    val shouldOpenBookedRide: Boolean,
    val shouldOpenConfirmRide: Boolean,
    val shouldOpenTipView: SingleStateEvent<Unit>,
    val markerLocations: List<NearByDriver>
) {

    companion object {
        val EMPTY = HomeState(
            startLocation = "",
            destinationLocation = "",
            savedAddresses = listOf(),
            profileData = ProfileDomainData.EMPTY,
            isOpenSearchClicked = false,
            error = "",
            isScheduleClicked = false,
            isAddSavePlaceClicked = false,
            isLoading = false,
            isScheduleTheRideClicked = false,
            isMyLocationButtonClicked = false,
            locationModel = LocationModel.EMPTY,
            isLocationAdded = false,
            shouldOpenBookedRide = false,
            shouldOpenConfirmRide = false,
            shouldOpenTipView = SingleStateEvent(),
            markerLocations = listOf()
        )
    }
}

sealed interface HomeEvent {
    object OnSearchClicked : HomeEvent
    object LoadAddresses : HomeEvent
    object LoadProfile : HomeEvent
    object OnSearchOpened : HomeEvent
    object OnAllSavedAddressClicked : HomeEvent
    object ScheduleButtonClicked : HomeEvent
    object ScheduleTheRideClicked : HomeEvent
    object ObserveTripState: HomeEvent
}