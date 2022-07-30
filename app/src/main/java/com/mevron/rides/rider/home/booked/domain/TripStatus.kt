package com.mevron.rides.rider.home.booked.domain

enum class TripStatus(val status: String) {
    ACCEPTED("accepted"),
    DRIVER_ARRIVED("driver_arrived"),
    TRIP_STARTED("trip_began"),
    TRIP_COMPLETED("completed"),
    START_RIDE("start_ride"),
    UNKNOWN("unknown")
}

fun String.toTripStatus(): TripStatus =
    TripStatus.values().firstOrNull { it.status == this } ?: TripStatus.UNKNOWN