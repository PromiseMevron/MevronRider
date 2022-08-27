package com.mevron.rides.rider.home.booked.domain

import com.mevron.rides.rider.socket.domain.models.MetaData

const val UNDEFINED_COORDINATE = Double.NEGATIVE_INFINITY

data class BookedTripState(
    val currentStatus: TripStatus,
    val pickupLatitude: Double,
    val pickupLongitude: Double,
    val dropOffLatitude: Double,
    val dropOffLongitude: Double,
    val pickupAddress: String,
    val destinationAddress: String,
    val isLocationProcessed: Boolean,
    val metaData: MetaData?,
    val rating: Int,
    val tipRider: Int,
    val review: MutableList<String>,
    val isSuccess: Boolean,
    val error: String,
    val loading: Boolean,
    val customRating: String
) {

    val hasValidCoordinates: Boolean
        get() = pickupLatitude != UNDEFINED_COORDINATE && pickupLongitude != UNDEFINED_COORDINATE &&
                dropOffLatitude != UNDEFINED_COORDINATE && dropOffLongitude != UNDEFINED_COORDINATE

    companion object {
        val EMPTY = BookedTripState(
            currentStatus = TripStatus.UNKNOWN,
            pickupLatitude = UNDEFINED_COORDINATE,
            pickupLongitude = UNDEFINED_COORDINATE,
            dropOffLatitude = UNDEFINED_COORDINATE,
            dropOffLongitude = UNDEFINED_COORDINATE,
            pickupAddress = "",
            destinationAddress = "",
            isLocationProcessed = false,
            metaData = null,
            tipRider = 0,
            rating = 1,
            review = mutableListOf(),
            isSuccess = false,
            error = "",
            loading = false,
            customRating = ""
        )
    }
}

sealed interface BookedTripEvent {
    object RequestTripStatus : BookedTripEvent
}