package com.mevron.rides.rider.authentication.ui.profilesetup.name

import app.cash.turbine.test
import com.mevron.rides.rider.authentication.ui.profilesetup.name.event.NameSignUpEvent
import com.mevron.rides.rider.authentication.ui.profilesetup.name.state.NameSignUpState
import com.mevron.rides.rider.sharedprefrence.domain.repository.IPreferenceRepository
import com.mevron.rides.rider.sharedprefrence.domain.usescases.SetPreferenceUseCase
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class NameSignUpViewModelTest{

    private val useCase: SetPreferenceUseCase = mockk()
    private val viewModel = NameSignUpViewModel(useCase)
    private val clearReop = mockk<IPreferenceRepository>()
    private val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp(){
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun `when NameSignUpViewModel#updateState is invoked - state is updated`() = runBlocking {
        viewModel.updateState(name = "aa bb")
        viewModel.state.test {
           assertEquals(
                NameSignUpState.EMPTY.copy(name = "aa bb", firstName = "aa", lastName = "bb"),
                awaitItem()
            )
        }
    }

    @Test
    fun `when NameSignUpViewModel#onEvent is invoked for OnButtonClick  - ViewModel process event`() {
        every { useCase(any(), any()) }.just(Runs)
        viewModel.onEvent(NameSignUpEvent.OnBottonClicked)
        verify { useCase("FIRST_NAME", "") }
        verify { useCase("LAST_NAME", "") }
    }
}