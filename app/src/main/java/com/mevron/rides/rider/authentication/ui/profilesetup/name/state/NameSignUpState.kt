package com.mevron.rides.rider.authentication.ui.profilesetup.name.state

data class NameSignUpState(
    val name: String,
    val firstName: String,
    val lastName: String,
    val isSuccess: Boolean,
    val openNextScreen: Boolean

) {
    companion object {
        val EMPTY = NameSignUpState(
            name = "",
            firstName = "",
            lastName = "",
            isSuccess = false,
          openNextScreen = false
        )
    }
}
