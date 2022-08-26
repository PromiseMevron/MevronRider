package com.mevron.rides.rider.settings.referal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.settings.referal.data.model.ReferalReport
import com.mevron.rides.rider.settings.referal.domain.model.ReferalNumber
import com.mevron.rides.rider.settings.referal.domain.model.ReferralData
import com.mevron.rides.rider.settings.referal.domain.usecase.GetReferalDetailUseCase
import com.mevron.rides.rider.settings.referal.ui.event.ReferalEvent
import com.mevron.rides.rider.settings.referal.ui.state.ReferralDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReferalDetailViewModel @Inject constructor(private val useCase: GetReferalDetailUseCase) :
    ViewModel() {

    private val mutableState: MutableStateFlow<ReferralDetailState> =
        MutableStateFlow(ReferralDetailState.EMPTY)

    val state: StateFlow<ReferralDetailState>
        get() = mutableState

    fun handleEvent(event: ReferalEvent) {
        when (event) {
            ReferalEvent.GetReferalDetail -> getReferalHistory()
            ReferalEvent.SetReferalDetail -> {}
            ReferalEvent.GetReferalPrefDetail -> {
            }
        }
    }

    private fun getReferalHistory() {
        val data = toData()
        updateState(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = useCase(data)) {
                is DomainModel.Success -> {
                    val response = result.data as ReferalNumber
                    updateState(
                        isLoading = false,
                        numberOfRides = response.rides
                    )
                }
                is DomainModel.Error -> mutableState.update {
                    mutableState.value.copy(
                        isLoading = false,
                        error = "Failure to set referral"
                    )
                }
            }
        }
    }

    private fun toData(): ReferalReport {
        val currentValue = mutableState.value
        return ReferalReport(
            from = currentValue.startDate,
            to = currentValue.endDate,
            referral_id = currentValue.referalID
        )
    }

    fun updateState(
        isLoading: Boolean? = null,
        data: List<ReferralData>? = null,
        setReferal: Boolean? = null,
        numberOfRides: String? = null,
        referalID: String? = null,
        referralStatus: Int? = null,
        referralCode: String? = null,
        setCode: String? = null,
        error: String? = null,
        succes: Boolean? = null,
        startDate: String? = null,
        endDate: String? = null
    ) {
        val currentValue = mutableState.value
        mutableState.update {
            currentValue.copy(
                isLoading = isLoading ?: currentValue.isLoading,
                refData = data ?: currentValue.refData,
                setCode = setCode ?: currentValue.setCode,
                setReferal = setReferal ?: currentValue.setReferal,
                numberOfRides = numberOfRides ?: currentValue.numberOfRides,
                referalID = referalID ?: currentValue.referalID,
                referralCode = referralCode ?: currentValue.referralCode,
                referralStatus = referralStatus ?: currentValue.referralStatus,
                error = error ?: currentValue.error,
                succes = succes ?: currentValue.succes,
                startDate = startDate ?: currentValue.startDate,
                endDate = endDate ?: currentValue.endDate
            )
        }
    }
}