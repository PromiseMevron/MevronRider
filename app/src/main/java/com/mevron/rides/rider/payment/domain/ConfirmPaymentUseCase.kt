package com.mevron.rides.rider.payment.domain

import javax.inject.Inject
class ConfirmPaymentUseCase @Inject constructor(private val repository: IPaymentOptionsRepository) {
    suspend operator fun invoke(identifier: String) = repository.confirmPayment(identifier)
}