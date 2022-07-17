package com.mevron.rides.rider.payment.ui

import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.TripState
import com.mevron.rides.rider.domain.usecase.GetTripStateUseCase
import com.mevron.rides.rider.home.booked.domain.TripStatus
import com.mevron.rides.rider.payment.domain.GetPaymentMethodsUseCase
import com.mevron.rides.rider.payment.domain.PaymentCardDomainModel
import com.mevron.rides.rider.remote.MevronRepo
import com.mevron.rides.rider.shared.ui.BaseViewModel
import com.mevron.rides.rider.shared.ui.SingleStateEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val getPaymentMethodsUseCase: GetPaymentMethodsUseCase,
    private val getTripStateUseCase: GetTripStateUseCase
) : BaseViewModel<PaymentViewState, PaymentViewEvent>() {

    override fun createInitialState(): PaymentViewState = PaymentViewState.EMPTY

    private fun observeTripState() {
        viewModelScope.launch {
            getTripStateUseCase().collect { tripState ->

                if (tripState is TripState.TripStatusState) {
                    val tripStatus = tripState.data.metaData
                    if (tripStatus.status == TripStatus.ACCEPTED.status) {
                        setState {
                            copy(
                                startLocationAddress = tripStatus.trip.pickupAddress,
                                destinationAddress = tripStatus.trip.destinationAddress,
                                startLocationLat = tripStatus.trip.pickupLatitude.toDouble(),
                                startLocationLng = tripStatus.trip.pickupLongitude.toDouble(),
                                endLocationLat = tripStatus.trip.destinationLatitude.toDouble(),
                                endLocationLng = tripStatus.trip.destinationLongitude.toDouble()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getPaymentMethods() {
        setState { copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val result = getPaymentMethodsUseCase()
            if (result is DomainModel.Success) {
                val data = result.data as PaymentCardDomainModel
                setState { copy(isLoading = false, paymentCards = data.cards, error = "") }
            } else {
                setState { copy(error = (result as DomainModel.Error).error.toString()) }
            }
        }
    }

    override fun setEvent(event: PaymentViewEvent) {
        when (event) {
            PaymentViewEvent.AddCardClicked -> setState {
                copy(
                    addPaymentClicked = SingleStateEvent<Unit>().apply { set(Unit) },
                    error = ""
                )
            }

            PaymentViewEvent.GetPaymentMethods -> getPaymentMethods()
        }
    }

    fun resolveCoordinateRendered() {
        setState { copy(isCoordinateRendered = true) }
    }

    init {
        observeTripState()
    }
}