package com.mevron.rides.rider.authentication.ui.profilesetup.email

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.authentication.data.models.createaccount.SaveDetailsRequest
import com.mevron.rides.rider.authentication.domain.usecase.CreateAccountUseCase
import com.mevron.rides.rider.authentication.ui.profilesetup.email.event.RegisterEmailEvent
import com.mevron.rides.rider.authentication.ui.profilesetup.email.state.EmailSignUpState
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.sharedprefrence.domain.usescases.GetPreferenceUseCase
import com.mevron.rides.rider.sharedprefrence.domain.usescases.SetPreferenceUseCase
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.Constants.COUNTRY
import com.mevron.rides.rider.util.Constants.EMAIL
import com.mevron.rides.rider.util.Constants.FIRST_NAME
import com.mevron.rides.rider.util.Constants.LAST_NAME
import com.mevron.rides.rider.util.Constants.PHONE_NUMBER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailSignViewModel @Inject constructor(
    private val setPreferenceUseCase: SetPreferenceUseCase,
    private val createAccountUseCase: CreateAccountUseCase,
    private val getPreferenceUseCase: GetPreferenceUseCase
) : ViewModel() {

    private val mutableState: MutableStateFlow<EmailSignUpState> =
        MutableStateFlow(EmailSignUpState.EMPTY)

    val state: StateFlow<EmailSignUpState>
        get() = mutableState

    private fun registerEmail(saveDetailsRequest: SaveDetailsRequest) {
        updateState(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = createAccountUseCase(saveDetailsRequest)) {
                is DomainModel.Success -> {
                    setPreferenceUseCase(EMAIL, state.value.email)
                    updateState(
                        isLoading = false,
                        isSuccess = true,
                    )
                }
                is DomainModel.Error -> mutableState.update {
                    mutableState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = result.toState()
                    )
                }
            }
        }
    }


    fun onEvent(event: RegisterEmailEvent) {
        when (event) {
            RegisterEmailEvent.NextButtonClick -> {
                registerEmail(mutableState.value.buildRequest())
            }
        }
    }

    private fun EmailSignUpState.buildRequest(): SaveDetailsRequest =
        SaveDetailsRequest(
            email = email,
            firstName = getPreferenceUseCase(FIRST_NAME),
            lastName = getPreferenceUseCase(LAST_NAME),
            phoneNumber = getPreferenceUseCase(PHONE_NUMBER),
            country = getPreferenceUseCase(COUNTRY)
        )

    private fun DomainModel.Error.toState(): String =
        this.error.localizedMessage ?: "Account creation failed"

    fun updateState(
        email: String? = null,
        isLoading: Boolean? = false,
        isCorrect: Boolean? = false,
        isSuccess: Boolean? = false,
        error: String? = null,
        phone: String? = null
    ) {
        val currentValue = mutableState.value
        mutableState.update {
            currentValue.copy(
                email = email ?: currentValue.email,
                isLoading = isLoading ?: currentValue.isLoading,
                isCorrect = isCorrect ?: currentValue.isCorrect,
                isSuccess = isSuccess ?: currentValue.isSuccess,
                error = error ?: currentValue.error,
                phoneNuumber = phone ?: currentValue.phoneNuumber
            )
        }
    }
}