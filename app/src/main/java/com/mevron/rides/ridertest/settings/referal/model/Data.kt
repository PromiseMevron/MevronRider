package com.mevron.rides.ridertest.settings.referal.model

data class Data(
    val current_page: Int,
    val from: Int,
    val last_page: Int,
    val per_page: Int,
    val referralCode: Any,
    val referralStatus: Int,
    val result: List<Result>,
    val to: Int
)