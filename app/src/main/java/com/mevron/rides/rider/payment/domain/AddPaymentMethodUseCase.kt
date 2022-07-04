package com.mevron.rides.rider.payment.domain

import com.mevron.rides.rider.home.model.AddCard
import javax.inject.Inject

class AddPaymentMethodUseCase @Inject constructor(private val repository: IPaymentOptionsRepository) {

    suspend operator fun invoke(addCard: AddCard) = repository.addCard(addCard)
}