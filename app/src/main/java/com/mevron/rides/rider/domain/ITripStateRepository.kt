package com.mevron.rides.rider.domain

import kotlinx.coroutines.flow.StateFlow

interface ITripStateRepository {
    val currentTripState: StateFlow<TripState>

    fun setTripState(tripState: TripState)
}