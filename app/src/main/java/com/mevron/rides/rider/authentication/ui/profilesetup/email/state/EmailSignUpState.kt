package com.mevron.rides.rider.authentication.ui.profilesetup.email.state

data class EmailSignUpState(
    val email: String,
    val name: String,
    val isCorrect: Boolean,
    val isLoading: Boolean,
    val isSuccess: Boolean,
    val error: String,
    val country: String,
    val phoneNuumber: String,
) {
    companion object {
        val EMPTY = EmailSignUpState(
            email = "",
            name = "",
            isCorrect = false,
            isLoading = false,
            isSuccess = false,
            error = "",
            phoneNuumber = "",
            country = ""
        )
    }
}
