package com.mevron.rides.rider.supportpages.data.model

data class NotificationData(
    val current_page: Int,
    val from: Int,
    val last_page: Int,
    val per_page: Int,
    val result: List<NotificationBody>,
    val to: Int
)

data class NotificationBody(
    val id: Int,
    val title:String,
    val description:String,
    val createAt: String
)

