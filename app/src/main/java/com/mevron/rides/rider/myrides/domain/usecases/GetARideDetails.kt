package com.mevron.rides.rider.myrides.domain.usecases

import com.mevron.rides.rider.myrides.domain.repository.IMyRideRepo
import javax.inject.Inject

class GetARideDetails @Inject constructor(private val repository: IMyRideRepo) {
    suspend operator fun invoke(identifier: String) = repository.getASpecificRide(identifier = identifier)
}