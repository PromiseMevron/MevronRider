package com.mevron.rides.rider.savedplaces.ui.addressdetails

import app.cash.turbine.test
import com.mevron.rides.rider.savedplaces.domain.usecase.SaveAddressUseCase
import com.mevron.rides.rider.savedplaces.ui.addressdetails.event.SaveAddressDetailsEvent
import com.mevron.rides.rider.savedplaces.ui.addressdetails.state.SaveAddressDetailState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SaveAddressDetailsViewModelTest {

    val useCase = mockk<SaveAddressUseCase>()
    val viewModel = SaveAddressDetailsViewModel(useCase)
    val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        coEvery { useCase(any()) }.coAnswers { mockk() }
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun `whenSaveAddressDetailViewModel#updateState is invoked - state is updated`() = runBlocking {
        viewModel.updateState(isLoading = true, backButton = true)
        viewModel.state.test {
            assertEquals(
                SaveAddressDetailState.EMPTY.copy(isLoading = true, backButton = true),
                awaitItem()
            )
        }
    }

    @Test
    fun `when SaveAddressDetailViewModel#onEvent#AddNew is invoked for OnButtonClick  - ViewModel process event`() {
        viewModel.handleEvent(SaveAddressDetailsEvent.SaveAddressClicked)
        coVerify { useCase(any()) }
    }

    @Test
    fun `when SaveAddressDetailViewModel#onEvent#backbutton is invoked for OnButtonClick  - ViewModel process event`() =
        runBlocking {
            viewModel.handleEvent(SaveAddressDetailsEvent.BackButtonPressed)
            viewModel.state.test {
                assertEquals(
                    SaveAddressDetailState.EMPTY.copy(backButton = true),
                    awaitItem()
                )
            }
        }

}