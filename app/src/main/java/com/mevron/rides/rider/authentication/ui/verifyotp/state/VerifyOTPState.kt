package com.mevron.rides.rider.authentication.ui.verifyotp.state

data class VerifyOTPState(
    val phoneNumber: String,
    val code: String,
    val isNew: Boolean,
    val isRequestSuccess: Boolean,
    val isLoading: Boolean,
    val uiid: String,
    val error: String,
    val accessToken: String
) {
    companion object {
        val EMPTY = VerifyOTPState(
            phoneNumber = "",
            code = "",
            isNew = true,
            isRequestSuccess = false,
            isLoading = false,
            uiid = "",
            accessToken = "",
            error = ""
        )
    }
}
