package com.mevron.rides.rider.payment.data

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.payment.domain.IRatingRepository
import com.mevron.rides.rider.payment.domain.RateData
import com.mevron.rides.rider.remote.HTTPErrorHandler
import com.mevron.rides.rider.util.Constants

class RatingRepository(private val ratingApi: RatingApi) : IRatingRepository {

    override suspend fun rateDriver(rateData: RateData): DomainModel {
        val request = ratingApi.rateDriver(rateData.toRequest())
        return if (request.isSuccessful) {
            DomainModel.Success(data = Unit)
        } else {
            val error = HTTPErrorHandler.handleErrorWithCode(request)
            DomainModel.Error(Throwable(error?.error?.message ?: Constants.UNEXPECTED_ERROR))
        }
    }

    private fun RateData.toRequest(): RateRequest = RateRequest(tripId, rating)
}