package com.mevron.rides.rider.payment.data

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.home.model.AddCard
import com.mevron.rides.rider.home.model.GetLinkAmount
import com.mevron.rides.rider.home.model.getCard.Data
import com.mevron.rides.rider.home.model.getCard.WalletData
import com.mevron.rides.rider.payment.domain.*
import com.mevron.rides.rider.remote.HTTPErrorHandler
import com.mevron.rides.rider.util.Constants

// TODO add unit test for this class
class PaymentOptionsRepository(private val api: PaymentOptionsApi) : IPaymentOptionsRepository {

    override suspend fun deleteCard(identifier: String): DomainModel {
        return try {
            val response = api.deleteCard(identifier)
            if (response.isSuccessful) {
                DomainModel.Success(data = Unit)
            } else {
                val error = HTTPErrorHandler.handleErrorWithCode(response)
                DomainModel.Error(Throwable(error?.error?.message ?: Constants.UNEXPECTED_ERROR))
            }
        } catch (error: Throwable) {
            DomainModel.Error(Throwable(Constants.UNEXPECTED_ERROR))
        }
    }

    override suspend fun addCard(data: AddCard): DomainModel {
        return try {
            val response = api.addCard(data)
            if (response.isSuccessful) {
                DomainModel.Success(data = Unit)
            } else {
                val error = HTTPErrorHandler.handleErrorWithCode(response)
                DomainModel.Error(Throwable(error?.error?.message ?: Constants.UNEXPECTED_ERROR))
            }
        } catch (error: Throwable) {
            DomainModel.Error(Throwable(Constants.UNEXPECTED_ERROR))
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
                val error = HTTPErrorHandler.handleErrorWithCode(response)
                DomainModel.Error(Throwable(error?.error?.message ?: Constants.UNEXPECTED_ERROR))
            }
        } catch (error: Throwable) {
            DomainModel.Error(Throwable(Constants.UNEXPECTED_ERROR))
        }
    }

    override suspend fun getPaymentLink(data: GetLinkAmount): DomainModel {
        return try {
            val response = api.getPaymentLink(data = data)
            if (response.isSuccessful) {
                DomainModel.Success(
                    data = PaymentLinkDomain(response.body()?.link ?: "")
                 /*   response.body()?.success?.data?.toDomainModel()
                        ?: PaymentCardDomainModel()*/
                )
            } else {
                val error = HTTPErrorHandler.handleErrorWithCode(response)
                DomainModel.Error(Throwable(error?.error?.message ?: Constants.UNEXPECTED_ERROR))
            }
        } catch (error: Throwable) {
            DomainModel.Error(Throwable(Constants.UNEXPECTED_ERROR))
        }
    }

    override suspend fun getWalletDetails(): DomainModel  = api.getWalletDetails().let {
        if (it.isSuccessful) {
            it.body()?.toDomainModel() ?: DomainModel.Error(Throwable(Constants.UNEXPECTED_ERROR))
        } else {
            val error = HTTPErrorHandler.handleErrorWithCode(it)
            DomainModel.Error(Throwable(error?.error?.message ?: Constants.UNEXPECTED_ERROR))
        }
    }

    override suspend fun addFund(data: CashActionData): DomainModel {
        return try {
            val response = api.addFund(data)
            if (response.isSuccessful) {
                DomainModel.Success(data = Unit)
            } else {
                val error = HTTPErrorHandler.handleErrorWithCode(response)
                DomainModel.Error(Throwable(error?.error?.message ?: Constants.UNEXPECTED_ERROR))
            }
        } catch (error: Throwable) {
            DomainModel.Error(Throwable(Constants.UNEXPECTED_ERROR))
        }
    }

    override suspend fun confirmPayment(uiid: String): DomainModel {
        return try {
            val response = api.confirmPayment(uiid.replace("%3F", "?"))
            if (response.isSuccessful) {
                DomainModel.Success(data = Unit)
            } else {
                val error = HTTPErrorHandler.handleErrorWithCode(response)
                DomainModel.Error(Throwable(error?.error?.message ?: Constants.UNEXPECTED_ERROR))
            }
        } catch (error: Throwable) {
            DomainModel.Error(Throwable(Constants.UNEXPECTED_ERROR))
        }
    }
}

private fun PaymentDetailsResponse.toDomainModel() = DomainModel.Success(
    data = PaymentDetailsDomainData(
        balance = "${this.paySuccess.payData.currencySymbol}${paySuccess.payData.balance}",
        data = this.paySuccess.payData.transactions.map {
            PaymentDetailsDomainDatum(
                amount = "${this.paySuccess.payData.currencySymbol}${it.amount}", date = it.date,
                icon = it.icon ?: "",
                method = it.method,
                narration = it.narration,
                time = it.time
            )
        }
    )
)

private fun WalletData.toDomainModel(): PaymentCardDomainModel {
    val card = this.card
    return PaymentCardDomainModel(
        cards = card.map { it.toDomainModel() }.apply { toMutableList().add(PaymentCard.EMPTY.copy(type = "cash")) },
        balance = this.balance.toDoubleOrNull() ?: 0.0
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


