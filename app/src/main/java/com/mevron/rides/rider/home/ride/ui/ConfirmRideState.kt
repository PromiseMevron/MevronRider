package com.mevron.rides.rider.home.ride.ui

import com.mevron.rides.rider.home.booked.domain.UNDEFINED_COORDINATE

data class ConfirmRideState(
    val paymentType: PaymentType,
    val payId: String,
    val vehicleId: String,
    val uuid: String,
    val startLocationLat: Double,
    val startLocationLng: Double,
    val endLocationLat: Double,
    val endLocationLng: Double,
    val startLocationAddress: String,
    val destinationAddress: String,
    val isLoading: Boolean,
    val isRideConfirmed: Boolean,
    val isRideConfirmedFailed: Boolean,
    val isCoordinateRendered: Boolean,
    val isSearchingDrivers: Boolean,
    val isBookingConfirmed: Boolean,
    val isRideCancelled: Boolean,
    val error: String,
    val reasonForCancel: String,
    val trip_Id: String
) {

    val isValidCoordinate: Boolean
        get() = startLocationLat != UNDEFINED_COORDINATE && startLocationLng != UNDEFINED_COORDINATE
                && endLocationLat != UNDEFINED_COORDINATE && endLocationLng != UNDEFINED_COORDINATE

    companion object {
        val EMPTY = ConfirmRideState(
            paymentType = PaymentType.CASH,
            uuid = "",
            startLocationLat = UNDEFINED_COORDINATE,
            startLocationLng = UNDEFINED_COORDINATE,
            endLocationLat = UNDEFINED_COORDINATE,
            endLocationLng = UNDEFINED_COORDINATE,
            startLocationAddress = "",
            destinationAddress = "",
            isLoading = false,
            isRideConfirmed = false,
            isCoordinateRendered = false,
            isSearchingDrivers = false,
            isBookingConfirmed = false,
            error = "",
            payId = "cash",
            vehicleId = "",
            reasonForCancel = "",
            trip_Id = "",
            isRideCancelled = false,
            isRideConfirmedFailed = false
        )
    }
}

enum class PaymentType(val value: String) {
    CASH("cash"),
    CREDIT_CARD("card")
}