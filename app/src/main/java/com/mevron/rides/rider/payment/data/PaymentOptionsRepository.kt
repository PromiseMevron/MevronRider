package com.mevron.rides.rider.payment.data

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.payment.domain.IPaymentOptionsRepository

class PaymentOptionsRepository : IPaymentOptionsRepository {
    override suspend fun deleteCard(identifier: String): DomainModel {
        TODO("Not yet implemented")
    }
}