package com.mevron.rides.rider.settings

data class SettingsState(val isSuccess: Boolean, val error: String, val loader: Boolean) {
    companion object{
        val EMPTY = SettingsState(
            isSuccess = false,
            error = "",
            loader = false
        )
    }
}