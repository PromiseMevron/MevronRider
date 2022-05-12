package com.mevron.rides.rider.supportpages.ui.promo.state

import com.mevron.rides.rider.supportpages.domain.model.Supports

data class PromoState(
    val isLoading: Boolean,
    val isSuccess: Boolean,
    val error: String,
    val data: MutableList<Supports>,
    val backButton: Boolean
) {
    companion object {
        val EMPTY = PromoState(
            isLoading = false,
            isSuccess = false,
            error = "",
            data = mutableListOf(),
            backButton = false
        )
    }
}