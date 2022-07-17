package com.mevron.rides.rider.home.booked.domain

const val UNDEFINED_COORDINATE = Double.NEGATIVE_INFINITY

data class BookedTripState(
    val currentStatus: TripStatus,
    val pickupLatitude: Double,
    val pickupLongitude: Double,
    val dropOffLatitude: Double,
    val dropOffLongitude: Double,
    val pickupAddress: String,
    val destinationAddress: String,
    val isLocationProcessed: Boolean
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
            isLocationProcessed = false
        )
    }
}

sealed interface BookedTripEvent {
    object RequestTripStatus : BookedTripEvent
}