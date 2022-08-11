package com.mevron.rides.rider.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.home.model.GetLinkAmount
import com.mevron.rides.rider.payment.domain.*
import com.mevron.rides.rider.payment.ui.GetLinkSavedState
import com.mevron.rides.rider.payment.ui.GetLinkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedPaymentViewModel @Inject constructor(
    private val useCase: GetPaymentLinkUseCase,
    private val getPaymentMethodsUseCase: GetPaymentMethodsUseCase,
) : ViewModel() {

    private val mutableState: MutableStateFlow<GetLinkSavedState> =
        MutableStateFlow(GetLinkSavedState.EMPTY)

    val state: StateFlow<GetLinkSavedState>
        get() = mutableState

    fun getPayLink() {
        updateState(isLoading = true)
        val request = mutableState.value.toRequest()
        viewModelScope.launch(Dispatchers.IO) {

            when (val result = useCase(request)) {
                is DomainModel.Success -> {
                    val data = result.data as PaymentLinkDomain
                    updateState(
                        isLoading = false,
                        payLink = data.link
                    )
                }
                is DomainModel.Error -> mutableState.update {
                    mutableState.value.copy(
                        isLoading = false,
                        error = result.buildString()
                    )
                }
            }
        }
    }

    private fun DomainModel.Error.buildString(): String =
        this.error.localizedMessage ?: "Card Addition Failed"


    private fun GetLinkState.toRequest(): GetLinkAmount =
        GetLinkAmount(
            amount = this.amount
        )

    fun getPaymentMethods() {
        val theData: MutableList<PaymentCard> = mutableListOf()
        updateState(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = getPaymentMethodsUseCase()
            if (result is DomainModel.Success) {
                theData.add(PaymentCard.EMPTY)
                val data = result.data as PaymentCardDomainModel
                theData.addAll(data.cards)
                updateState(paymentCards = theData, isLoading = false)
            } else {
                updateState(error = (result as DomainModel.Error).error.toString())
            }
        }
    }

    private fun GetLinkSavedState.toRequest(): GetLinkAmount =
        GetLinkAmount(
            amount = this.amount
        )


    fun updateState(
        payLink: String? = null,
        isLoading: Boolean? = null,
        error: String? = null,
        amount: String? = null,
        paymentCards: MutableList<PaymentCard>? = null
    ) {
        val currentValue = mutableState.value
        mutableState.update {
            currentValue.copy(
                paymentLink = payLink ?: currentValue.paymentLink,
                isLoading = isLoading ?: currentValue.isLoading,
                error = error ?: currentValue.error,
                amount = amount ?: currentValue.amount,
                paymentCards = paymentCards ?: currentValue.paymentCards
            )
        }
    }

}
