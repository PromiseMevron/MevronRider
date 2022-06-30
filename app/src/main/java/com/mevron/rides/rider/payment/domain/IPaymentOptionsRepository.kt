package com.mevron.rides.rider.payment.domain

import com.mevron.rides.rider.domain.DomainModel

interface IPaymentOptionsRepository {

    suspend fun deleteCard(identifier: String): DomainModel
}