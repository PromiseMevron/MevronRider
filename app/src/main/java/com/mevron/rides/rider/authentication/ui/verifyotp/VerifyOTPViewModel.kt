package com.mevron.rides.rider.authentication.ui.verifyotp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.authentication.data.models.verifyotp.ValidateOTPRequest
import com.mevron.rides.rider.authentication.domain.model.VerifyOTPDomainModel
import com.mevron.rides.rider.authentication.domain.usecase.VerifyOTPUseCase
import com.mevron.rides.rider.authentication.ui.verifyotp.event.VerifyOTPEvent
import com.mevron.rides.rider.authentication.ui.verifyotp.state.VerifyOTPState
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.sharedprefrence.domain.usescases.SetPreferenceUseCase
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.Constants.PHONE_NUMBER
import com.mevron.rides.rider.util.Constants.TOKEN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class VerifyOTPViewModel @Inject constructor(
    private val verifyOTPUseCase: VerifyOTPUseCase,
    private val setPreferenceUseCase: SetPreferenceUseCase
) : ViewModel() {
    private val mutableState: MutableStateFlow<VerifyOTPState> =
        MutableStateFlow(VerifyOTPState.EMPTY)

    val state: StateFlow<VerifyOTPState>
        get() = mutableState

    private fun verifyOTP() {
        updateState(isLoading = true)
        val request = mutableState.value.toRequest()
        viewModelScope.launch(Dispatchers.IO) {

            when (val result = verifyOTPUseCase(request)) {
                is DomainModel.Success -> {
                    val data = result.data as VerifyOTPDomainModel
                    setPreferenceUseCase(TOKEN, data.accessToken)
                    setPreferenceUseCase(Constants.UUID, data.uuid)
                    setPreferenceUseCase(PHONE_NUMBER, mutableState.value.phoneNumber)
                    updateState(
                        isLoading = false,
                        isRequestSuccess = true,
                        accessToken = data.accessToken,
                        uiid = data.uuid,
                        isNew = data.riderType.lowercase(Locale.getDefault()) == "new".lowercase(
                            Locale.getDefault()
                        )
                    )
                }
                is DomainModel.Error -> mutableState.update {
                    mutableState.value.copy(
                        isLoading = false,
                        isRequestSuccess = false,
                        error = result.buildString()
                    )
                }
            }
        }
    }

    fun onEvent(event: VerifyOTPEvent) {
        when (event) {
            is VerifyOTPEvent.OnOTPComplete -> {
                verifyOTP()
            }
        }
    }

    private fun VerifyOTPState.toRequest(): ValidateOTPRequest =
        ValidateOTPRequest(
            code = this.code,
            phoneNumber = this.phoneNumber
        )

    private fun DomainModel.Error.buildString(): String =
        this.error.localizedMessage ?: "OTP verification  failed"

    fun updateState(
        phoneNumber: String? = null,
        code: String? = null,
        isNew: Boolean? = true,
        isRequestSuccess: Boolean? = false,
        isLoading: Boolean? = false,
        error: String? = null,
        accessToken: String? = null,
        uiid: String? = null
    ) {
        val currentValue = mutableState.value
        mutableState.update {
            currentValue.copy(
                phoneNumber = phoneNumber ?: currentValue.phoneNumber,
                code = code ?: currentValue.code,
                isNew = isNew ?: currentValue.isNew,
                isRequestSuccess = isRequestSuccess ?: currentValue.isRequestSuccess,
                isLoading = isLoading ?: currentValue.isLoading,
                accessToken = accessToken ?: currentValue.accessToken,
                uiid = uiid ?: currentValue.uiid,
                error = error ?: currentValue.error
            )
        }
    }
}