package com.mevron.rides.rider.authentication.ui.profilesetup.name

import androidx.lifecycle.ViewModel
import com.mevron.rides.rider.authentication.ui.profilesetup.name.event.NameSignUpEvent
import com.mevron.rides.rider.authentication.ui.profilesetup.name.state.NameSignUpState
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.sharedprefrence.domain.usescases.SetPreferenceUseCase
import com.mevron.rides.rider.util.Constants.FIRST_NAME
import com.mevron.rides.rider.util.Constants.LAST_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NameSignUpViewModel @Inject constructor(
    private val setPreferenceUseCase: SetPreferenceUseCase
) : ViewModel() {

    private val mutableState: MutableStateFlow<NameSignUpState> =
        MutableStateFlow(NameSignUpState.EMPTY)

    val state: StateFlow<NameSignUpState>
        get() = mutableState

    fun onEvent(event: NameSignUpEvent) {
        when (event) {
            NameSignUpEvent.OnBottonClicked -> {
                finaliseNameSignUp()
            }
        }
    }

    private fun finaliseNameSignUp(){
        setPreferenceUseCase(FIRST_NAME, state.value.firstName)
        setPreferenceUseCase(LAST_NAME, state.value.lastName)
        updateState(openNestScreen = true)
    }

    fun updateState(
        name: String? = null,
        isSuccess: Boolean? = false,
        openNestScreen: Boolean? = false,
        firstName: String? = null,
        lastName: String? = null,
    ) {
        val currentValue = mutableState.value

        mutableState.update {
            currentValue.copy(
                name = name ?: currentValue.name,
                firstName = firstName ?: currentValue.firstName,
                lastName = lastName ?: currentValue.lastName,
                isSuccess = isSuccess ?: currentValue.isSuccess,
                openNextScreen = openNestScreen ?: currentValue.openNextScreen
            )
        }
    }

}