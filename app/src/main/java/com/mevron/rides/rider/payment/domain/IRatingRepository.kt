package com.mevron.rides.rider.payment.domain

import com.mevron.rides.rider.domain.DomainModel

interface IRatingRepository {
    suspend fun rateDriver(rateData: RateData): DomainModel
}