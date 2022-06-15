package com.mevron.rides.rider.emerg.data.model

import com.google.gson.annotations.SerializedName

data class GetContactSuccess(
    @SerializedName("data")
    val contactData: List<Data>,
    val message: String,
    val status: String
)