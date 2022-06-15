package com.mevron.rides.rider.sharedprefrence.domain.usescases

import com.mevron.rides.rider.sharedprefrence.domain.repository.IPreferenceRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Test


class GetPreferenceUseCaseTest {

    private val repo: IPreferenceRepository = mockk()
    private val useCase = GetPreferenceUseCase(repo)

    @Test
    fun `when GetPreferenceUseCase is invoked - returns data from repository`() {
        every { repo.getStringForKey("KEY") }.returns("TEST")
        val result = useCase("KEY")
        assert(result == "TEST")
    }
}