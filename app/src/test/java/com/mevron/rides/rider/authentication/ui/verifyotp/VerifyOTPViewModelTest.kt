package com.mevron.rides.rider.authentication.ui.verifyotp

import app.cash.turbine.test
import com.mevron.rides.rider.authentication.domain.usecase.VerifyOTPUseCase
import com.mevron.rides.rider.authentication.ui.verifyotp.event.VerifyOTPEvent
import com.mevron.rides.rider.authentication.ui.verifyotp.state.VerifyOTPState
import com.mevron.rides.rider.sharedprefrence.domain.usescases.SetPreferenceUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class VerifyOTPViewModelTest {

    val useCase = mockk<VerifyOTPUseCase>()
    val savePrefUseCase = mockk<SetPreferenceUseCase>()
    val viewModel = VerifyOTPViewModel(useCase, savePrefUseCase)

    val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        coEvery { useCase(any()) }.coAnswers { mockk() }
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun `whenVerifyOTPViewModel#updateState is invoked - state is updated`() = runBlocking {
        viewModel.updateState(isLoading = true, code = "aa")
        viewModel.state.test {
            assertEquals(
                VerifyOTPState.EMPTY.copy(isLoading = true, code = "aa"),
                awaitItem()
            )
        }
    }

    @Test
    fun `when AccountCreationViewModel#onEvent is invoked for OnButtonClick  - ViewModel process event`() {
        viewModel.onEvent(VerifyOTPEvent.OnOTPComplete)
        coVerify { useCase(any()) }
    }


}