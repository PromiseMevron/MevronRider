package com.mevron.rides.rider.supportpages.ui.promo

import app.cash.turbine.test
import com.mevron.rides.rider.supportpages.domain.usecase.GetPromoUseCase
import com.mevron.rides.rider.supportpages.ui.promo.event.PromoEvents
import com.mevron.rides.rider.supportpages.ui.promo.state.PromoState
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
class PromoViewModelTest {
    val useCase = mockk<GetPromoUseCase>()
    val viewModel = PromoViewModel(useCase)
    val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun `whenPromoViewModel#updateState is invoked - state is updated`() = runBlocking {
        viewModel.updateState(isLoading = true, backButton = true)
        viewModel.state.test {
            assertEquals(
                PromoState.EMPTY.copy(isLoading = true, backButton = true),
                awaitItem()
            )
        }
    }

    @Test
    fun `when PromoViewModel#onEvent#AddNew is invoked for OnButtonClick  - ViewModel process event`() =
        runBlocking {
            viewModel.handleEvent(PromoEvents.GetPromotions)
            coVerify { useCase() }
        }

    @Test
    fun `when PromoViewModel#onEvent#backbutton is invoked for OnButtonClick  - ViewModel process event`() =
        runBlocking {
            viewModel.handleEvent(PromoEvents.OnBackButtonClicked)
            viewModel.state.test {
                assertEquals(
                    PromoState.EMPTY.copy(backButton = true),
                    awaitItem()
                )
            }
        }
}