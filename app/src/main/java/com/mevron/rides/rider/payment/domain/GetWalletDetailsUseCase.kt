package com.mevron.rides.rider.payment.domain

import javax.inject.Inject

class GetWalletDetailsUseCase @Inject constructor(private val repository: IPaymentOptionsRepository) {
    suspend operator fun invoke() = repository.getWalletDetails()
}