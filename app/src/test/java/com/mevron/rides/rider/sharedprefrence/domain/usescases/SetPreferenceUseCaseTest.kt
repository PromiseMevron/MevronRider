package com.mevron.rides.rider.sharedprefrence.domain.usescases

import com.mevron.rides.rider.sharedprefrence.domain.repository.IPreferenceRepository
import io.mockk.*
import org.junit.After
import org.junit.Test


class SetPreferenceUseCaseTest {

    private val repo: IPreferenceRepository = mockk()
    private val useCase = SetPreferenceUseCase(repo)

    @Test
    fun `test that when the preference is called there is an interaction`() {
        every { repo.setStringForKey("KEY", "ABC") }.just(Runs)
        useCase("KEY", "ABC")
        verify { repo.setStringForKey("KEY", "ABC") }
    }

}