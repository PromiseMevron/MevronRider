package com.mevron.rides.rider.payment.domain

import com.mevron.rides.rider.payment.data.RateDriverRequest
import javax.inject.Inject

class RateRiderUseCase @Inject constructor(private val repository: ITipAndReviewRepository) {

    suspend operator fun invoke(data: RateDriverRequest) = repository.rateRider(data)
}