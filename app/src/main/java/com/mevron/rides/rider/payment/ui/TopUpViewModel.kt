package com.mevron.rides.rider.payment.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.home.model.GetLinkAmount
import com.mevron.rides.rider.payment.data.CashActionData
import com.mevron.rides.rider.payment.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopUpViewModel @Inject constructor(
    private val linkCase: GetPaymentLinkUseCase,
    private val addFundUseCase: AddFundUseCase,
    private val getPaymentMethodsUseCase: GetPaymentMethodsUseCase
) : ViewModel() {
    private val mutableState: MutableStateFlow<TopUpState> =
        MutableStateFlow(TopUpState.EMPTY)

    val state: StateFlow<TopUpState>
        get() = mutableState

     fun addFundToWallet() {
         Log.d("we reached here", "we reached here 1111111")
        updateState(loading = true)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = addFundUseCase(data =   CashActionData(
                amount = "500",
                card_id = "dc2ae65a-523a-4968-9fdd-269dd2d7c741"))) {
                is DomainModel.Success -> {
                    updateState(
                        loading = false,
                        errorMessage = "",
                        successFund = true
                    )
                }
                is DomainModel.Error -> mutableState.update {
                    mutableState.value.copy(
                        loading = false,
                        errorMessage = "Failure to add Fund",
                    )
                }
            }
        }
    }

     fun getCards() {
        updateState(loading = true)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = getPaymentMethodsUseCase()) {
                is DomainModel.Success -> {
                    val dd = result.data as PaymentCardDomainModel
                    Log.d("THE CARDS ARE", "THE CARDS ARE 2 $dd")
                    updateState(
                        loading = false,
                        errorMessage = "",
                        successCard = true,
                        cardData = dd.cards
                    )
                }
                is DomainModel.Error -> mutableState.update {
                    mutableState.value.copy(
                        loading = false,
                        errorMessage = "Failure to get card",
                    )
                }
            }
        }
    }

    fun getPayLink() {
        Log.d("we reached here", "we reached here 5656555555")
        updateState(loading = true)
        val request = mutableState.value.toLinkRequest()
        val dt =  CashActionData(amount = "500", card_id = "dc2ae65a-523a-4968-9fdd-269dd2d7c741")
        viewModelScope.launch(Dispatchers.IO) {
            when (addFundUseCase(dt)) {
                is DomainModel.Success -> {
                    updateState(
                        loading = false,
                        errorMessage = "",
                        successFund = true
                    )
                }
                is DomainModel.Error -> mutableState.update {
                    mutableState.value.copy(
                        loading = false,
                        errorMessage = "Failure to add Fund",
                    )
                }
            }
        }
     /*   viewModelScope.launch(Dispatchers.IO) {
            when (val result = linkCase(request)) {
                is DomainModel.Success -> {
                    val data = result.data as PaymentLinkDomain
                    updateState(
                        loading = false,
                        payLink = data.link
                    )
                }
                is DomainModel.Error -> mutableState.update {
                    mutableState.value.copy(
                        loading = false,
                        errorLink = "Failure to get payment link"
                    )
                }
            }
        }*/
    }

    private fun TopUpState.toLinkRequest(): GetLinkAmount =
        GetLinkAmount(
            amount = this.addFundAmount
        )

    fun updateState(
        loading: Boolean? = null,
        errorMessage: String? = null,
        balance: String? = null,
        success: Boolean? = null,
        data: List<PaymentBalanceDetailsDomainDatum>? = null,
        date: String? = null,
        cashOut: String? = null,
        addFund: String? = null,
        cardNumber: String? = null,
        cardData: List<PaymentCard>? = null,
        successFund: Boolean? = null,
        successCard: Boolean? = null,
        error: String? = null,
        payLink: String? = null,
        addCard: Boolean? = null,
        shouldGoBack: Boolean? = null
    ) {
        val currentState = mutableState.value
        mutableState.update {
            currentState.copy(
                loading = loading ?: currentState.loading,
                errorMessage = errorMessage ?: currentState.errorMessage,
                balance = balance ?: currentState.balance,
                success = success ?: currentState.success,
                data = data ?: currentState.data,
                date = date ?: currentState.date,
                cashOutAmount = cashOut ?: currentState.cashOutAmount,
                addFundAmount = addFund ?: currentState.addFundAmount,
                cardNumber = cardNumber ?: currentState.cardNumber,
                cardData = cardData ?: currentState.cardData,
                successFund = successFund ?: currentState.successFund,
                successCard = successCard ?: currentState.successCard,
                errorLink = error ?: currentState.errorLink,
                payLink = payLink ?: currentState.payLink,
                addCard = addCard ?: currentState.addCard,
                shouldGoBack = shouldGoBack ?: currentState.shouldGoBack
            )
        }
    }
}