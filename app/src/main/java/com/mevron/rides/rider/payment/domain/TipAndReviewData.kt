package com.mevron.rides.rider.payment.domain

class TipAndReviewData(
    val tripId: String,
    val tipAmount: Int,
    val comment: List<String>
)