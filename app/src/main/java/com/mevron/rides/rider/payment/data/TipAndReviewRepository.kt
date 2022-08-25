package com.mevron.rides.rider.payment.data

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.payment.domain.ITipAndReviewRepository
import com.mevron.rides.rider.payment.domain.TipAndReviewData

class TipAndReviewRepository(private val api: TipAndReviewApi) : ITipAndReviewRepository {

    override suspend fun sendTipAndReview(tipAndReviewData: TipAndReviewData): DomainModel {
        val result = api.sendTipAndReview(tipAndReviewData.toRequest())
        return if (result.isSuccessful) {
            DomainModel.Success(data = Unit)
        } else {
            DomainModel.Error(Throwable(result.errorBody().toString()))
        }
    }

    override suspend fun rateRider(rateDriverRequest: RateDriverRequest): DomainModel {
        val result = api.sendRating(rateDriverRequest)
        return if (result.isSuccessful) {
            DomainModel.Success(data = Unit)
        } else {
            DomainModel.Error(Throwable(result.errorBody().toString()))
        }
    }
}

private fun TipAndReviewData.toRequest(): TipAndReviewRequest =
    TipAndReviewRequest(
        tripId,
        tipAmount,
        comment
    )