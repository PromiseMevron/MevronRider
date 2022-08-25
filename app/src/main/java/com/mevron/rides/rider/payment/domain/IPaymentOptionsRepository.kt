package com.mevron.rides.rider.payment.domain

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.home.model.AddCard
import com.mevron.rides.rider.home.model.GetLinkAmount
import com.mevron.rides.rider.payment.data.CashActionData

interface IPaymentOptionsRepository {

    suspend fun deleteCard(identifier: String): DomainModel

    suspend fun addCard(data: AddCard): DomainModel

    suspend fun getCards(): DomainModel

    suspend fun getPaymentLink(data: GetLinkAmount): DomainModel

    suspend fun getWalletDetails(): DomainModel

    suspend fun addFund(data: CashActionData): DomainModel

}