package com.mevron.rides.rider.data

import com.mevron.rides.rider.domain.IDriverLocationRepository
import com.mevron.rides.rider.home.model.DriverLocationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DriverLocationRepository : IDriverLocationRepository {
    private val mutableDriverState: MutableStateFlow<DriverLocationModel> =
        MutableStateFlow(DriverLocationModel.EMPTY)

    override val currentDriverState: StateFlow<DriverLocationModel>
        get() = mutableDriverState

    override fun settDriverState(locData: DriverLocationModel) {
        mutableDriverState.value = locData
    }
}