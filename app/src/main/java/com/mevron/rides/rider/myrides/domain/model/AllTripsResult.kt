package com.mevron.rides.rider.myrides.domain.model


data class GetAllTripsDomainData(val data: List<AllTripsResult>)

class AllTripsResult (
    val amount: String,
    val date: String,
    val id: String,
    val status: String,
    val time: String,
    val title: String,
    val vehicleName: String,
    val vehicleNum: String,
    val destinationLatitude: String,
    val destinationLongitude: String,
    val pickupLatitude: String,
    val pickupLongitude: String,
    val driverProfile: String,
    )
