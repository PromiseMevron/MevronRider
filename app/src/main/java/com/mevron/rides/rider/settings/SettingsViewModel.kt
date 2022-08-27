package com.mevron.rides.rider.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.settings.domain.LogOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val useCase: LogOutUseCase) : ViewModel() {

    private val mutableState: MutableStateFlow<SettingsState> =
        MutableStateFlow(SettingsState.EMPTY)
    val state: StateFlow<SettingsState>
        get() = mutableState

    fun logOutDevice() {
        updateState(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = useCase()) {
                is DomainModel.Error -> mutableState.update {
                    mutableState.value.copy(
                        loader = false,
                        isSuccess = false,
                        error = "Error in logging out of device"
                    )
                }
                is DomainModel.Success -> {
                    updateState(
                        isLoading = false,
                        isSuccess = true,
                        error = ""
                    )
                }
            }
        }
    }

    fun updateState(
        isLoading: Boolean? = null,
        isSuccess: Boolean? = null,
        error: String? = null,

        ) {
        val currentState = mutableState.value
        mutableState.update {
            currentState.copy(
                loader = isLoading ?: currentState.loader,
                isSuccess = isSuccess ?: currentState.isSuccess,
                error = error ?: currentState.error,
            )
        }
    }
}