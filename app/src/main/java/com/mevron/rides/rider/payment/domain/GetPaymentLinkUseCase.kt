package com.mevron.rides.rider.payment.domain

import com.mevron.rides.rider.home.model.GetLinkAmount
import javax.inject.Inject

class GetPaymentLinkUseCase @Inject constructor(private val repository: IPaymentOptionsRepository) {
    suspend operator fun invoke(data: GetLinkAmount) = repository.getPaymentLink(data)
}