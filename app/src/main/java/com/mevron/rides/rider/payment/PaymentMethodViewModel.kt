package com.mevron.rides.rider.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.home.model.GetLinkAmount
import com.mevron.rides.rider.payment.domain.ConfirmPaymentUseCase
import com.mevron.rides.rider.payment.domain.GetPaymentLinkUseCase
import com.mevron.rides.rider.payment.domain.PaymentLinkDomain
import com.mevron.rides.rider.payment.ui.GetLinkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentMethodViewModel @Inject constructor(
    private val useCase: GetPaymentLinkUseCase,
    private val confirmUseCase: ConfirmPaymentUseCase
) :
    ViewModel() {

    private val mutableState: MutableStateFlow<GetLinkState> =
        MutableStateFlow(GetLinkState.EMPTY)

    val state: StateFlow<GetLinkState>
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

    fun confirmPayment() {
        val uuid = mutableState.value.confirmLink
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = confirmUseCase(uuid)) {
                else -> {
                    updateState(
                        isLoading = false,
                        error = "",
                        successFund = true
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


    fun updateState(
        payLink: String? = null,
        isLoading: Boolean? = null,
        error: String? = null,
        amount: String? = null,
        confirmLink: String? = null,
        successFund: Boolean? = null
    ) {
        val currentValue = mutableState.value
        mutableState.update {
            currentValue.copy(
                paymentLink = payLink ?: currentValue.paymentLink,
                isLoading = isLoading ?: currentValue.isLoading,
                error = error ?: currentValue.error,
                amount = amount ?: currentValue.amount,
                confirmLink = confirmLink ?: currentValue.confirmLink,
                successFund = successFund ?: currentValue.successFund
            )
        }
    }

}
