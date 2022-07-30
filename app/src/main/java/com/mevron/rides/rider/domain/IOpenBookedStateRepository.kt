package com.mevron.rides.rider.domain

import kotlinx.coroutines.flow.StateFlow

interface IOpenBookedStateRepository {
    val currentTripState: StateFlow<Boolean>

    fun setTripState(tripState: Boolean)
}