package com.mevron.rides.rider.payment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.payment.domain.GetWalletDetailsUseCase
import com.mevron.rides.rider.payment.domain.PaymentBalanceDetailsDomainDatum
import com.mevron.rides.rider.payment.domain.PaymentDetailsDomainData
import com.mevron.rides.rider.payment.domain.PaymentDetailsDomainDatum
import com.mevron.rides.rider.util.toReadableDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor (private val useCase: GetWalletDetailsUseCase,) : ViewModel() {

    private val mutableState: MutableStateFlow<GetWalletDetailState> =
        MutableStateFlow(GetWalletDetailState.EMPTY)

    val state: StateFlow<GetWalletDetailState>
        get() = mutableState

    fun onEvent(event: WalletEvent) {
        when (event) {
            WalletEvent.GetWalletDetail -> getWalletDetails()
        }
    }

    private fun getWalletDetails() {
        updateState(loading = true)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = useCase()) {
                is DomainModel.Success -> {
                    val data = result.data as PaymentDetailsDomainData
                    updateState(
                        loading = false,
                        errorMessage = "",
                        balance = data.balance,
                    )
                    createSection(data)
                }
                is DomainModel.Error -> mutableState.update {
                    mutableState.value.copy(
                        loading = false,
                        errorMessage = "Failure to get your wallet details",
                        balance = ""
                    )
                }
            }
        }
    }
    private fun createSection(data: PaymentDetailsDomainData) {
        val arr = data.data.sortedByDescending {
            it.date.replace("-", "")
        }
        val arr1 = mutableListOf<PaymentBalanceDetailsDomainDatum>()
        var arr2 = mutableListOf<PaymentDetailsDomainDatum>()
        var headingDate = ""

        for (i in arr.indices) {
            if (i == 0) {
                arr[i].apply {
                    headingDate = this.date.toReadableDate()
                    arr2.add(PaymentDetailsDomainDatum(amount, date, icon, method, narration, time))
                }
            } else {
                if (arr[i - 1].date == arr[i].date) {
                    arr[i].apply {
                        headingDate = this.date.toReadableDate()
                        arr2.add(
                            PaymentDetailsDomainDatum(
                                amount,
                                date,
                                icon,
                                method,
                                narration,
                                time
                            )
                        )
                    }
                } else {
                    arr1.add(PaymentBalanceDetailsDomainDatum(date = headingDate, data = arr2))
                    arr2 = mutableListOf()
                    arr[i].apply {
                        headingDate = this.date.toReadableDate()
                        arr2.add(
                            PaymentDetailsDomainDatum(
                                amount,
                                date,
                                icon,
                                method,
                                narration,
                                time
                            )
                        )
                    }
                }
            }
            if (i == arr.size - 1) {
                arr1.add(PaymentBalanceDetailsDomainDatum(date = headingDate, data = arr2))
            }
        }
        updateState(data = arr1)
    }



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
        successFund: Boolean? = null,
        successCard: Boolean? = null,
        error: String? = null,
        payLink: String? = null
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
                successFund = successFund ?: currentState.successFund,
                successCard = successCard ?: currentState.successCard,
                errorLink = error ?: currentState.errorLink,
                payLink = payLink ?: currentState.payLink
            )
        }
    }

}