package com.mevron.rides.rider.payment.domain

import javax.inject.Inject

class GetPaymentMethodsUseCase @Inject constructor(private val repository: IPaymentOptionsRepository) {
    suspend operator fun invoke() = repository.getCards()
}