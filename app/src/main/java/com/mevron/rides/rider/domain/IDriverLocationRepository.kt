package com.mevron.rides.rider.domain

import com.mevron.rides.rider.home.model.DriverLocationModel
import kotlinx.coroutines.flow.StateFlow

interface IDriverLocationRepository {
    val currentDriverState: StateFlow<DriverLocationModel>

    fun settDriverState(locData: DriverLocationModel)
}