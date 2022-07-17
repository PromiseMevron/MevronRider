package com.mevron.rides.rider.domain

import com.mevron.rides.rider.socket.domain.models.DriverSearchData
import com.mevron.rides.rider.socket.domain.models.NearByDriversData
import com.mevron.rides.rider.socket.domain.models.StateMachineData
import com.mevron.rides.rider.socket.domain.models.TripStatus

sealed interface TripState {
    data class NearByDriversState(val data: NearByDriversData) : TripState
    data class TripStatusState(val data: TripStatus) : TripState
    data class DriverSearchState(val data: DriverSearchData) : TripState
    data class StateMachineState(val data: StateMachineData) : TripState
    object Idle : TripState
}