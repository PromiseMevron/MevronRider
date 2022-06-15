package com.mevron.rides.rider.myrides.domain.usecases

import com.mevron.rides.rider.myrides.domain.repository.IMyRideRepo
import com.mevron.rides.rider.savedplaces.domain.repository.IAddressRepository
import javax.inject.Inject

class GetScheduleRideUseCase @Inject constructor(private val repository: IMyRideRepo) {
    suspend operator fun invoke() = repository.getScheduledRide()
}