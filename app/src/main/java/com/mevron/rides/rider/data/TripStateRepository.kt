package com.mevron.rides.rider.data

import com.mevron.rides.rider.domain.ITripStateRepository
import com.mevron.rides.rider.domain.TripState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TripStateRepository : ITripStateRepository{

    private val mutableTripState: MutableStateFlow<TripState> = MutableStateFlow(TripState.Idle)

    override val currentTripState: StateFlow<TripState>
        get() = mutableTripState

    override fun setTripState(tripState: TripState) {
        mutableTripState.value = tripState
    }
}