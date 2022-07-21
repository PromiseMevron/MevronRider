package com.mevron.rides.rider.payment.ui

import com.mevron.rides.rider.payment.domain.PaymentCard
import com.mevron.rides.rider.shared.ui.SingleStateEvent
import com.mevron.rides.rider.home.booked.domain.UNDEFINED_COORDINATE

data class PaymentViewState(
    val startLocationLat: Double,
    val startLocationLng: Double,
    val endLocationLat: Double,
    val endLocationLng: Double,
    val startLocationAddress: String,
    val destinationAddress: String,
    val paymentCards: List<PaymentCard>,
    val isLoading: Boolean,
    val selectedPaymentCard: PaymentCard,
    val addPaymentClicked: SingleStateEvent<Unit>,
    val isCoordinateRendered: Boolean,
    val openConfirmRide: SingleStateEvent<Unit>,
    val error: String
) {
    companion object {
        val EMPTY = PaymentViewState(
            startLocationLat = UNDEFINED_COORDINATE,
            startLocationLng = UNDEFINED_COORDINATE,
            endLocationLat = UNDEFINED_COORDINATE,
            endLocationLng = UNDEFINED_COORDINATE,
            startLocationAddress = "",
            destinationAddress = "",
            paymentCards = listOf(),
            isLoading = false,
            selectedPaymentCard = PaymentCard.EMPTY,
            addPaymentClicked = SingleStateEvent(),
            isCoordinateRendered = false,
            openConfirmRide = SingleStateEvent(),
            error = ""
        )
    }

    val isValidCoordinate: Boolean
        get() = startLocationLat != UNDEFINED_COORDINATE && startLocationLng != UNDEFINED_COORDINATE
                && endLocationLat != UNDEFINED_COORDINATE && endLocationLng != UNDEFINED_COORDINATE
}

sealed interface PaymentViewEvent {
    object AddCardClicked : PaymentViewEvent
    object GetPaymentMethods : PaymentViewEvent
    object OpenConfirmRide : PaymentViewEvent
}