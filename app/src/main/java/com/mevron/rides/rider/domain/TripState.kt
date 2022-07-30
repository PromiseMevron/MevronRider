package com.mevron.rides.rider.domain

import com.mevron.rides.rider.socket.domain.models.*

sealed interface TripState {
    data class NearByDriversState(val data: NearByDriversData) : TripState
    data class TripStatusState(val data: TripStatus) : TripState
    data class DriverSearchState(val data: DriverSearchData) : TripState
    data class StateMachineState(val data: StateMachineSocketResponse) : TripState
    object  RideAccepted : TripState
    object Idle : TripState
}