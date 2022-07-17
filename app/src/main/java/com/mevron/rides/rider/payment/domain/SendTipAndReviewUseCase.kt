package com.mevron.rides.rider.payment.domain

import javax.inject.Inject

class SendTipAndReviewUseCase @Inject constructor(private val repository: ITipAndReviewRepository) {

    suspend operator fun invoke(data: TipAndReviewData) = repository.sendTipAndReview(data)
}