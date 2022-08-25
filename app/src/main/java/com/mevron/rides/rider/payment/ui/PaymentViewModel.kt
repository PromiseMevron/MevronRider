package com.mevron.rides.rider.payment.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.TripState
import com.mevron.rides.rider.domain.usecase.GetOrderPropertiesUseCase
import com.mevron.rides.rider.domain.usecase.GetTripStateUseCase
import com.mevron.rides.rider.domain.usecase.SetOrderPropertiesUseCase
import com.mevron.rides.rider.home.booked.domain.TripStatus
import com.mevron.rides.rider.payment.domain.GetPaymentMethodsUseCase
import com.mevron.rides.rider.payment.domain.PaymentCard
import com.mevron.rides.rider.payment.domain.PaymentCardDomainModel
import com.mevron.rides.rider.shared.ui.BaseViewModel
import com.mevron.rides.rider.shared.ui.SingleStateEvent
import com.mevron.rides.rider.util.Constants.DROP_OFF_ADD
import com.mevron.rides.rider.util.Constants.DROP_OFF_LAT
import com.mevron.rides.rider.util.Constants.DROP_OFF_LNG
import com.mevron.rides.rider.util.Constants.PICK_UP_ADD
import com.mevron.rides.rider.util.Constants.PICK_UP_LAT
import com.mevron.rides.rider.util.Constants.PICK_UP_LNG
import com.mevron.rides.rider.util.Constants.ThePaymentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val getPaymentMethodsUseCase: GetPaymentMethodsUseCase,
    private val getTripStateUseCase: GetTripStateUseCase, // X cancel this
    private val getOrderPropertiesUseCase: GetOrderPropertiesUseCase,
    private val setOrderPropertiesUseCase: SetOrderPropertiesUseCase
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
                                startLocationAddress = getOrderPropertiesUseCase(PICK_UP_ADD),
                                destinationAddress = getOrderPropertiesUseCase(DROP_OFF_ADD),
                                startLocationLat = getOrderPropertiesUseCase(PICK_UP_LAT).toDouble(),
                                startLocationLng = getOrderPropertiesUseCase(PICK_UP_LNG).toDouble(),
                                endLocationLat = getOrderPropertiesUseCase(DROP_OFF_LAT).toDouble(),
                                endLocationLng = getOrderPropertiesUseCase(DROP_OFF_LNG).toDouble()
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateLocationStatus() {
        Log.d("Param", getOrderPropertiesUseCase(PICK_UP_LAT))
        Log.d("Param", getOrderPropertiesUseCase(PICK_UP_ADD))
        setState {
            copy(
                startLocationAddress = getOrderPropertiesUseCase(PICK_UP_ADD),
                destinationAddress = getOrderPropertiesUseCase(DROP_OFF_ADD),
                startLocationLat = getOrderPropertiesUseCase(PICK_UP_LAT).toDouble(),
                startLocationLng = getOrderPropertiesUseCase(PICK_UP_LNG).toDouble(),
                endLocationLat = getOrderPropertiesUseCase(DROP_OFF_LAT).toDouble(),
                endLocationLng = getOrderPropertiesUseCase(DROP_OFF_LNG).toDouble()
            )
        }
    }

    private fun getPaymentMethods() {
        val theData: MutableList<PaymentCard> = mutableListOf()
        setState { copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val result = getPaymentMethodsUseCase()
            if (result is DomainModel.Success) {
                theData.add(PaymentCard.EMPTY)
                val data = result.data as PaymentCardDomainModel
                theData.addAll(data.cards)
                setState { copy(isLoading = false, paymentCards = theData, error = "") }
            } else {
                setState { copy(error = (result as DomainModel.Error).error.toString()) }
            }
        }
    }

    fun setUpPaymentMethod(id: String){
        setOrderPropertiesUseCase(ThePaymentModel, id)
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
            PaymentViewEvent.OpenConfirmRide -> setState {
                copy(openConfirmRide = SingleStateEvent<Unit>().apply {
                    set(Unit)
                })
            }
        }
    }

    fun resolveCoordinateRendered() {
        setState { copy(isCoordinateRendered = true) }
    }

    fun markerAdded() {
        setState { copy(isMarkerRendered = true) }
    }

    init {
        observeTripState()
    }
}