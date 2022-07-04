package com.mevron.rides.rider.payment.data

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.home.model.AddCard
import com.mevron.rides.rider.payment.domain.IPaymentOptionsRepository
import com.mevron.rides.rider.payment.domain.PaymentCardDomainModel
import com.mevron.rides.rider.home.model.getCard.Data
import com.mevron.rides.rider.payment.domain.PaymentCard

// TODO add unit test for this
class PaymentOptionsRepository(private val api: PaymentOptionsApi) : IPaymentOptionsRepository {

    override suspend fun deleteCard(identifier: String): DomainModel {
        return try {
            val response = api.deleteCard(identifier)
            if (response.isSuccessful) {
                DomainModel.Success(data = Unit)
            } else {
                DomainModel.Error(Throwable(response.errorBody().toString()))
            }
        } catch (error: Throwable) {
            DomainModel.Error(Throwable("Error deleting card $error"))
        }
    }

    override suspend fun addCard(data: AddCard): DomainModel {
        return try {
            val response = api.addCard(data)
            if (response.isSuccessful) {
                DomainModel.Success(data = Unit)
            } else {
                DomainModel.Error(Throwable(response.errorBody().toString()))
            }
        } catch (error: Throwable) {
            DomainModel.Error(Throwable("Error adding card $error"))
        }
    }

    override suspend fun getCards(): DomainModel {
        return try {
            val response = api.getCards()
            if (response.isSuccessful) {
                DomainModel.Success(
                    data = response.body()?.success?.data?.toDomainModel()
                        ?: PaymentCardDomainModel()
                )
            } else {
                DomainModel.Error(Throwable(response.errorBody().toString()))
            }
        } catch (error: Throwable) {
            DomainModel.Error(Throwable("Error fetching cards $error"))
        }
    }
}

private fun List<Data>.toDomainModel(): PaymentCardDomainModel {
    return PaymentCardDomainModel(
        cards = map { it.toDomainModel() }
    )
}

private fun Data.toDomainModel(): PaymentCard =
    PaymentCard(
        bin = bin,
        brand = brand,
        expiryMonth = expiryMonth,
        expiryYear = expiryYear,
        lastDigits = lastDigits,
        type = type,
        uuid = uuid
    )
