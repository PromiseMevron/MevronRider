package com.mevron.rides.rider.savedplaces.ui.addaddress

import app.cash.turbine.test
import com.mevron.rides.rider.savedplaces.domain.usecase.GetAddressUseCase
import com.mevron.rides.rider.savedplaces.ui.addaddress.event.AddSavedAddressEvent
import com.mevron.rides.rider.savedplaces.ui.addaddress.state.AddSavedPlaceState
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
class AddSavedPlaceViewModelTest {

    private val useCase = mockk<GetAddressUseCase>()
    private val viewModel = AddSavedPlaceViewModel(useCase)

    private val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun `whenAddSavedPlaceViewModel#updateState is invoked - state is updated`() = runBlocking {
        viewModel.updateState(isLoading = true, backButton = true)
        viewModel.state.test {
            assertEquals(
                AddSavedPlaceState.EMPTY.copy(isLoading = true, backButton = true),
                awaitItem()
            )
        }
    }

    @Test
    fun `when AddSavedPlaceViewModel#onEvent#AddNew is invoked for OnButtonClick  - ViewModel process event`() =
        runBlocking {
            viewModel.handleEvent(AddSavedAddressEvent.OnAddNewClicked)
            viewModel.state.test {
                assertEquals(
                    AddSavedPlaceState.EMPTY.copy(openNextPage = true),
                    awaitItem()
                )
            }
        }

    @Test
    fun `when AddSavedPlaceViewModel#onEvent#backbutton is invoked for OnButtonClick  - ViewModel process event`() =
        runBlocking {
            viewModel.handleEvent(AddSavedAddressEvent.OnBackButtonClicked)
            viewModel.state.test {
                assertEquals(
                    AddSavedPlaceState.EMPTY.copy(backButton = true),
                    awaitItem()
                )
            }
        }

    @Test
    fun `when AddSavedPlaceViewModel#onEvent is invoked for OnButtonClick  - ViewModel process event`() {
        viewModel.handleEvent(AddSavedAddressEvent.GetNewAddress)
        coVerify { useCase() }
    }
}