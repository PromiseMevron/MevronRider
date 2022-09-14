package com.mevron.rides.rider.myrides.domain.model

data class TripDetailDomainData (
    val dateAndTime: String,
    val carNumber: String,
    val departureTime: String,
    val arrivalTime: String,
    val departureAddress: String,
    val arrivalAddress: String,
    val driverName: String,
    val driverRating: String,
    val paymentType: String,
    val startLat: String,
    val startLng: String,
    val endLat: String,
    val endLng: String,
    val total: String,
    val driverProfile: String,
    val polyLine: String?
    )