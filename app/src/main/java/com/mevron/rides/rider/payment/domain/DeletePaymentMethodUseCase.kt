package com.mevron.rides.rider.payment.domain

import javax.inject.Inject

class DeletePaymentMethodUseCase @Inject constructor(private val repository: IPaymentOptionsRepository) {

    suspend operator fun invoke(identifier: String) = repository.deleteCard(identifier)
}