package com.mevron.rides.rider.authentication.data.models.profile

data class ProfileData(
    val email: String?,
    val emailStatus: Int?,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val phoneNumberStatus: Int?,
    val profilePicture: String?,
    val rating: Double?,
    val uuid: String?,
    val supportNumber: String?
)