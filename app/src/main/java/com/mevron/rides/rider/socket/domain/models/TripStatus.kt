package com.mevron.rides.rider.socket.domain.models

import com.google.gson.annotations.SerializedName

data class TripStatus(
    @SerializedName("current_state")
    val currentState: String,
    @SerializedName("meta_data")
    val metaData: TripStatusMetadata
)

data class TripStatusMetadata(
    @SerializedName("status")
    val status: String,
    @SerializedName("rider")
    val rider: RiderData,
    @SerializedName("driver")
    val driver: DriverData,
    @SerializedName("trip")
    val trip: TripData,
    @SerializedName("paymentMethod")
    val paymentMethod: PaymentMethodData,
    @SerializedName("fare")
    val fareData: FareData
)

data class RiderData(
    @SerializedName("rating")
    val rating: String,
    @SerializedName("profilePicture")
    val profilePicture: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String
)

data class DriverData(
    @SerializedName("rating")
    val rating: String,
    @SerializedName("profilePicture")
    val profilePicture: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("vehicle")
    val vehicle: VehicleData
)

data class VehicleData(
    @SerializedName("color")
    val color: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("plateNumber")
    val plateNumber: String
)

data class TripData(
    @SerializedName("tripId")
    val tripId: String,
    @SerializedName("verificationCode")
    val verificationCode: String,
    @SerializedName("pickupAddress")
    val pickupAddress: String,
    @SerializedName("pickupLatitude")
    val pickupLatitude: String,
    @SerializedName("pickupLongitude")
    val pickupLongitude: String,
    @SerializedName("destinationAddress")
    val destinationAddress: String,
    @SerializedName("destinationLatitude")
    val destinationLatitude: String,
    @SerializedName("destinationLongitude")
    val destinationLongitude: String,
    @SerializedName("estimatedPickupTime")
    val estimatedPickupTime: String
)

data class PaymentMethodData(
    @SerializedName("type")
    val type: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)

data class FareData(
    @SerializedName("fare")
    val fareList: List<Fare>
)

data class Fare(
    @SerializedName("name")
    val name: String,
    @SerializedName("amount")
    val amount: String
)