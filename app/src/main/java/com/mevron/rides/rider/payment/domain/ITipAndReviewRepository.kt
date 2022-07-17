package com.mevron.rides.rider.payment.domain

import com.mevron.rides.rider.domain.DomainModel

interface ITipAndReviewRepository {

    suspend fun sendTipAndReview(tipAndReviewData: TipAndReviewData): DomainModel
}