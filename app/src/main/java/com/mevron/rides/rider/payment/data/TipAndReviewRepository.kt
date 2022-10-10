package com.mevron.rides.rider.payment.data

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.payment.domain.ITipAndReviewRepository
import com.mevron.rides.rider.payment.domain.TipAndReviewData
import com.mevron.rides.rider.remote.HTTPErrorHandler
import com.mevron.rides.rider.util.Constants

class TipAndReviewRepository(private val api: TipAndReviewApi) : ITipAndReviewRepository {

    override suspend fun sendTipAndReview(tipAndReviewData: TipAndReviewData): DomainModel {
        val result = api.sendTipAndReview(tipAndReviewData.toRequest())
        return if (result.isSuccessful) {
            DomainModel.Success(data = Unit)
        } else {
            val error = HTTPErrorHandler.handleErrorWithCode(result)
            DomainModel.Error(Throwable(error?.error?.message ?: Constants.UNEXPECTED_ERROR))
        }
    }

    override suspend fun rateRider(rateDriverRequest: RateDriverRequest): DomainModel {
        val result = api.sendRating(rateDriverRequest)
        return if (result.isSuccessful) {
            DomainModel.Success(data = Unit)
        } else {
            val error = HTTPErrorHandler.handleErrorWithCode(result)
            DomainModel.Error(Throwable(error?.error?.message ?: Constants.UNEXPECTED_ERROR))
        }
    }
}

private fun TipAndReviewData.toRequest(): TipAndReviewRequest =
    TipAndReviewRequest(
        tripId,
        tipAmount,
        comment
    )