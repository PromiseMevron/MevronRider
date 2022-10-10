package com.mevron.rides.rider.emerg.ui

import com.mevron.rides.rider.emerg.data.model.Set
import com.mevron.rides.rider.emerg.domain.model.GetContactDomainData


data class EmergencyState(
    val isLoading: Boolean,
    val isSuccess: Boolean,
    val isSuccessAdd: Boolean,
    val error: String,
    val data: MutableList<Set>,
    val openNextPage: Boolean,
    val backButton: Boolean,
    val name: String,
    val phone: String,
    val result: MutableList<GetContactDomainData>
) {
    companion object {
        val EMPTY = EmergencyState(
            isLoading = false,
            isSuccess = false,
            isSuccessAdd = false,
            error = "",
            data = mutableListOf(),
            openNextPage = false,
            backButton = false,
            name = "",
            phone = "",
            result = mutableListOf()
        )
    }
}
