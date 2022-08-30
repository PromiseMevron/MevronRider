package com.mevron.rides.rider.home.domain

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.home.data.DeviceID

interface IProfileRepository {
    suspend fun getProfile(): DomainModel
    suspend fun sendToken(id : DeviceID): DomainModel
}