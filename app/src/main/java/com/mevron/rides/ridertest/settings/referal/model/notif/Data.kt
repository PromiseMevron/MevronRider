package com.mevron.rides.ridertest.settings.referal.model.notif

data class Data(
    val current_page: Int,
    val from: Int,
    val last_page: Int,
    val per_page: Int,
    val result: List<Any>,
    val to: Int
)