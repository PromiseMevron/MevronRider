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
        openNestScreen: Boolean? = false

    ) {
        val currentValue = mutableState.value

        val fullName = name?.split(" ")
        val fName = fullName?.get(0)
        var lName: String? = ""
        fullName?.size?.let {
            for (i in 1 until (it)){
                lName += fullName[i]
            }
        }
        if (lName.isNullOrEmpty()){
            lName = null
        }

        mutableState.update {
            currentValue.copy(
                name = name ?: currentValue.name,
                firstName = fName ?: currentValue.firstName,
                lastName = lName ?: currentValue.lastName,
                isSuccess = isSuccess ?: currentValue.isSuccess,
                openNextScreen = openNestScreen ?: currentValue.openNextScreen
            )
        }
    }

}