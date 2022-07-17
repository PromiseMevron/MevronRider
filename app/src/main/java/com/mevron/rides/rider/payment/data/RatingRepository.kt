package com.mevron.rides.rider.payment.data

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.payment.domain.IRatingRepository
import com.mevron.rides.rider.payment.domain.RateData

class RatingRepository(private val ratingApi: RatingApi) : IRatingRepository {

    override suspend fun rateDriver(rateData: RateData): DomainModel {
        val request = ratingApi.rateDriver(rateData.toRequest())
        return if (request.isSuccessful) {
            DomainModel.Success(data = Unit)
        } else {
            DomainModel.Error(Throwable(request.errorBody().toString()))
        }
    }

    private fun RateData.toRequest(): RateRequest = RateRequest(tripId, rating)
}