package com.mevron.rides.rider.home.domain

data class ProfileDomainData(
    val email: String?,
    val emailStatus: Int?,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?,
    val phoneNumberStatus: Int?,
    val profilePicture: String?,
    val rating: Double?,
    val uuid: String?
) {
    companion object {
        val EMPTY = ProfileDomainData(
            email = "",
            emailStatus = null,
            firstName = "",
            lastName = "",
            phoneNumber = "",
            phoneNumberStatus = null,
            profilePicture = "",
            rating = null,
            uuid = null
        )
    }
}