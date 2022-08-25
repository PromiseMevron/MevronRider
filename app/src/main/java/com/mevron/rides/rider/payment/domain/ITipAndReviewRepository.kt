package com.mevron.rides.rider.payment.domain

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.payment.data.RateDriverRequest

interface ITipAndReviewRepository {

    suspend fun sendTipAndReview(tipAndReviewData: TipAndReviewData): DomainModel
    suspend fun rateRider(rateDriverRequest: RateDriverRequest): DomainModel
}