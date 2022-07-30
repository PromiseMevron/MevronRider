package com.mevron.rides.rider.data

import com.mevron.rides.rider.domain.IOpenBookedStateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OpenBookedStateRepository: IOpenBookedStateRepository {

    private val mutableTripState: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val currentTripState: StateFlow<Boolean>
        get() = mutableTripState

    override fun setTripState(tripState: Boolean) {
        mutableTripState.value = tripState
    }
}