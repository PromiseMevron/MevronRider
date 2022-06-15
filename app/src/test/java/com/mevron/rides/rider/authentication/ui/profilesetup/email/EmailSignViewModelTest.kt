package com.mevron.rides.rider.authentication.ui.profilesetup.email

import app.cash.turbine.test
import com.mevron.rides.rider.authentication.domain.usecase.CreateAccountUseCase
import com.mevron.rides.rider.authentication.ui.profilesetup.email.event.RegisterEmailEvent
import com.mevron.rides.rider.authentication.ui.profilesetup.email.state.EmailSignUpState
import com.mevron.rides.rider.authentication.ui.profilesetup.name.event.NameSignUpEvent
import com.mevron.rides.rider.sharedprefrence.domain.repository.IPreferenceRepository
import com.mevron.rides.rider.sharedprefrence.domain.usescases.GetPreferenceUseCase
import com.mevron.rides.rider.sharedprefrence.domain.usescases.SetPreferenceUseCase
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class EmailSignViewModelTest{

    private val useCase = mockk<CreateAccountUseCase>()
    private val useCasePref: SetPreferenceUseCase = mockk()
    private val useCaseGetPref: GetPreferenceUseCase = mockk()
    private val viewModel = EmailSignViewModel(useCasePref, useCase, useCaseGetPref)

    private val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp(){
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun `when NameSignUpViewModel#updateState is invoked - state is updated`() = runBlocking {
        viewModel.updateState(email = "aa bb")
        viewModel.state.test {
            assertEquals(
                EmailSignUpState.EMPTY.copy(email = "aa bb"),
                awaitItem()
            )
        }
    }

    @Test
    fun `when NameSignUpViewModel#onEvent is invoked for OnButtonClick  - ViewModel process event`() {
        every { useCaseGetPref(any()) }.returns("")
        viewModel.onEvent(RegisterEmailEvent.NextButtonClick)
        coVerify { useCase(any()) }
    }

}