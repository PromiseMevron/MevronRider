package com.mevron.rides.rider.remote.model.getprofile

data class Data(
    val email: Any,
    val emailStatus: Int,
    val firstName: Any,
    val lastName: Any,
    val phoneNumber: String,
    val phoneNumberStatus: Int,
    val profilePicture: Any,
    val rating: Any,
    val uuid: String
)