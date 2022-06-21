package com.mevron.rides.rider.home.ui

import app.cash.turbine.test
import com.mevron.rides.rider.home.domain.GetProfileUseCase
import com.mevron.rides.rider.savedplaces.domain.usecase.GetAddressUseCase
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

internal class HomeViewModelTest {

    private val getAddressUseCase: GetAddressUseCase = mockk(relaxed = true)
    private val getProfileUseCase: GetProfileUseCase = mockk(relaxed = true)

    private val viewModel = HomeViewModel(getAddressUseCase, getProfileUseCase)

    private val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun `when state change - viewModel update state`(): Unit = runBlocking {
        viewModel.setEvent(HomeEvent.OnSearchClicked)
        viewModel.uiState.test {
            assertEquals(awaitItem().isOpenSearchClicked, true)
        }
    }

    // TODO finish this test on flow completed
}