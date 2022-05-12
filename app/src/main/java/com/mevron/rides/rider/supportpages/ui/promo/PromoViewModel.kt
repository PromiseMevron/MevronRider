package com.mevron.rides.rider.supportpages.ui.promo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.supportpages.domain.model.SupportDomainData
import com.mevron.rides.rider.supportpages.domain.model.Supports
import com.mevron.rides.rider.supportpages.domain.usecase.GetPromoUseCase
import com.mevron.rides.rider.supportpages.ui.notification.event.NotificationEvents
import com.mevron.rides.rider.supportpages.ui.promo.event.PromoEvents
import com.mevron.rides.rider.supportpages.ui.promo.state.PromoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromoViewModel @Inject constructor(private val useCase: GetPromoUseCase) : ViewModel() {

    private val mutableState: MutableStateFlow<PromoState> =
        MutableStateFlow(PromoState.EMPTY)

    val state: StateFlow<PromoState>
        get() = mutableState

    private fun getPromos() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = useCase()) {
                is DomainModel.Error -> mutableState.update {
                    mutableState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = ""
                    )
                }
                is DomainModel.Success -> {
                    val data = result.data as SupportDomainData
                    updateState(
                        isLoading = false,
                        isRequestSuccess = true,
                        notification = data.notifications.toMutableList()
                    )
                }
            }
        }
    }

    fun handleEvent(event: PromoEvents) {
        when (event) {
            is PromoEvents.GetPromotions ->
                getPromos()
            is PromoEvents.OnBackButtonClicked ->
                updateState(backButton = true)
        }
    }

    fun updateState(
        isLoading: Boolean? = null,
        isRequestSuccess: Boolean? = null,
        notification: MutableList<Supports>? = null,
        backButton: Boolean? = null
    ) {
        val currentState = mutableState.value
        mutableState.update {
            currentState.copy(
                isLoading = isLoading ?: currentState.isLoading,
                isSuccess = isRequestSuccess ?: currentState.isSuccess,
                data = notification ?: currentState.data,
                backButton = backButton ?: currentState.backButton
            )
        }
    }
}