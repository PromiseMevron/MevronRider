package com.mevron.rides.rider.supportpages.ui.notification

import app.cash.turbine.test
import com.mevron.rides.rider.supportpages.domain.usecase.GetNotificationUseCase
import com.mevron.rides.rider.supportpages.ui.notification.event.NotificationEvents
import com.mevron.rides.rider.supportpages.ui.notification.state.NotificationState
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
class NotificationViewModelTest {

    val useCase = mockk<GetNotificationUseCase>()
    val viewModel = NotificationViewModel(useCase)
    val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun `whenNotifViewModel#updateState is invoked - state is updated`() = runBlocking {
        viewModel.updateState(isLoading = true, backButton = true)
        viewModel.state.test {
            assertEquals(
                NotificationState.EMPTY.copy(isLoading = true, backButton = true),
                awaitItem()
            )
        }
    }

    @Test
    fun `when NotifViewModel#onEvent#AddNew is invoked for OnButtonClick  - ViewModel process event`() =
        runBlocking {
            viewModel.handleEvent(NotificationEvents.GetNotifications)
            coVerify { useCase() }
        }

    @Test
    fun `when NotifViewModel#onEvent#backbutton is invoked for OnButtonClick  - ViewModel process event`() =
        runBlocking {
            viewModel.handleEvent(NotificationEvents.OnBackButtonClicked)
            viewModel.state.test {
                assertEquals(
                    NotificationState.EMPTY.copy(backButton = true),
                    awaitItem()
                )
            }
        }

}