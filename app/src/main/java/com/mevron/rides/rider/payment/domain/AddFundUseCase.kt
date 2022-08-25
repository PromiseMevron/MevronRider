package com.mevron.rides.rider.payment.domain

import com.mevron.rides.rider.payment.data.CashActionData
import javax.inject.Inject

class AddFundUseCase @Inject constructor(private val repository: IPaymentOptionsRepository) {

    suspend operator fun invoke(data: CashActionData) = repository.addFund(data)
}