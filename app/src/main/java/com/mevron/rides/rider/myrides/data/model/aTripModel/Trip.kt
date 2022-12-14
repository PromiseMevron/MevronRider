package com.mevron.rides.rider.myrides.data.model.aTripModel

data class Trip(
    val amount: String,
    val cashCollected: String,
    val cityId: Int,
    val comment: Any,
    val createdAt: String,
    val currencyCode: Any,
    val destinationAddress: String,
    val destinationLatitude: String,
    val destinationLongitude: String,
    val distance: Any,
    val driverArrivedDestinationTimestamp: Any,
    val driverArrivedPickUpTimestamp: Any,
    val driverLocation: Any,
    val driverRating: Int,
    val driverStartRideTimestamp: Any,
    val driverVehicleId: Any,
    val driver_id: String,
    val duration: Any,
    val endTime: String,
    val estimatedDistance: String,
    val estimatedTime: String,
    val exact_driver_end_ride_location: Any,
    val exact_driver_start_ride_location: Any,
    val fareBreakdown: Any,
    val fares: Any,
    val id: Int,
    val isAccepted: Boolean,
    val isArrivedAtDestination: Boolean,
    val isCancelled: Boolean,
    val isDriverArrived: Boolean,
    val isRideCompleted: Boolean,
    val isRideStarted: Boolean,
    val maxEstimatedCost: String,
    val minEstimatedCost: String,
    val paymentMethod: String,
    val pickupAddress: String,
    val pickupLatitude: String,
    val pickupLongitude: String,
    val riderRating: Int,
    val rider_id: String,
    val scheduleDate: Any,
    val scheduleDeliveryTime: Any,
    val schedulePickupTime: Any,
    val scheduleTime: Any,
    val startTime: String,
    val status: String,
    val surgeMultiplier: String,
    val tipAmount: Any,
    val type: String,
    val updatedAt: String,
    val uuid: String,
    val vehicleType: String,
    val verificationCode: String,
    val polyLine: String?
)