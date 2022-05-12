package com.mevron.rides.rider.authentication.ui.registerphone

import app.cash.turbine.test
import com.mevron.rides.rider.authentication.domain.usecase.RegisterPhoneUseCase
import com.mevron.rides.rider.authentication.ui.registerphone.event.RegisterPhoneEvent
import com.mevron.rides.rider.authentication.ui.registerphone.state.RegisterPhoneState
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
class RegisterPhoneViewModelTest{

    private val useCase = mockk<RegisterPhoneUseCase>()
    private val viewModel = RegisterPhoneViewModel(useCase)

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp(){
        coEvery { useCase(any()) }.coAnswers { mockk() }
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `whenAccountCreationViewModel#updateState is invoked - state is updated`() = runBlocking {
        viewModel.updateState(loading = true, phoneNumber = "ww")
        viewModel.state.test {
            assertEquals(
                RegisterPhoneState.EMPTY.copy(loading = true, phoneNumber = "ww"),
                awaitItem()
            )
        }
    }

    @Test
    fun `when AccountCreationViewModel#onEvent is invoked for OnButtonClick  - ViewModel process event`() {
        viewModel.onEvent(RegisterPhoneEvent.NextButtonClick)
        coVerify { useCase(any()) }
    }
}