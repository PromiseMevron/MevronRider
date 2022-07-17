package com.mevron.rides.rider.payment.domain

import javax.inject.Inject

class RateDriverUseCase @Inject constructor(private val repository: IRatingRepository) {
    suspend operator fun invoke(rateData: RateData) = repository.rateDriver(rateData)
}