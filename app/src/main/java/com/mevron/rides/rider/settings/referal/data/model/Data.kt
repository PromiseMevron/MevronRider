package com.mevron.rides.rider.settings.referal.data.model

data class Data(
    val current_page: Int,
    val from: Int,
    val last_page: Int,
    val per_page: Int,
    val referralCode: String?,
    val referralStatus: Int,
    val result: List<Result>,
    val to: Int
)