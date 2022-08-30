package com.mevron.rides.rider.home.domain

import com.mevron.rides.rider.home.data.DeviceID
import javax.inject.Inject

class FCMTokenUseCase  @Inject constructor(private val repository: IProfileRepository) {
    suspend operator fun invoke(id: DeviceID) = repository.sendToken(id)
}