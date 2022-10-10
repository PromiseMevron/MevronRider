package com.mevron.rides.rider.authentication.ui.verifyotp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.auth.model.details.SaveResponse
import com.mevron.rides.rider.authentication.data.models.createaccount.SaveDetailsRequest
import com.mevron.rides.rider.authentication.data.models.verifyotp.ValidateOTPRequest
import com.mevron.rides.rider.authentication.domain.model.VerifyOTPDomainModel
import com.mevron.rides.rider.authentication.domain.usecase.VerifyOTPUseCase
import com.mevron.rides.rider.authentication.ui.verifyotp.event.VerifyOTPEvent
import com.mevron.rides.rider.authentication.ui.verifyotp.state.VerifyOTPState
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.remote.HTTPErrorHandler
import com.mevron.rides.rider.remote.MevronRepo
import com.mevron.rides.rider.remote.model.GeneralResponse
import com.mevron.rides.rider.sharedprefrence.domain.usescases.SetPreferenceUseCase
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.Constants.PHONE_NUMBER
import com.mevron.rides.rider.util.Constants.TOKEN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class VerifyOTPViewModel @Inject constructor(
    private val verifyOTPUseCase: VerifyOTPUseCase,
    private val setPreferenceUseCase: SetPreferenceUseCase,
    private val repository: MevronRepo
) : ViewModel() {
    private val mutableState: MutableStateFlow<VerifyOTPState> =
        MutableStateFlow(VerifyOTPState.EMPTY)

    val state: StateFlow<VerifyOTPState>
        get() = mutableState

    private fun checkNew(data: VerifyOTPDomainModel): Boolean{
        Log.d("THE DEFAULT", "THE DEFAULT 1 ${data.proceed}")
        Log.d("THE DEFAULT", "THE DEFAULT 2 ${data.riderType}")

        if (!data.proceed && (data.riderType.lowercase(Locale.getDefault()) != "new".lowercase(
                Locale.getDefault()))){
            setPreferenceUseCase(Constants.COMPLETE, "proceed")
        }
        return if (data.riderType.lowercase(Locale.getDefault()) == "new".lowercase(
                Locale.getDefault())){
            true
        }else{
            data.proceed
        }
    }

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
                        isNew = checkNew(data)
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

    private fun ResendOTP() {
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
                        isNew = checkNew(data)
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

    fun resendOTP(data: ValidateOTPRequest): LiveData<GenericStatus<GeneralResponse>> {

        val result = MutableLiveData<GenericStatus<GeneralResponse>>()

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = repository.resendOTP(data)
                if(response.isSuccessful){
                    result.postValue(GenericStatus.Success(response.body()))
                }
                else{
                    result.postValue(GenericStatus.Error(HTTPErrorHandler.handleErrorWithCode(response)))
                }
            }catch (ex: Exception){
                ex.printStackTrace()
                result.postValue(GenericStatus.Error(HTTPErrorHandler.httpFailWithCode(ex)))
            }
        }
        return result
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