package com.mevron.rides.rider.emerg.domain.usecase

import com.mevron.rides.rider.emerg.domain.repository.IEmergencyRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class GetContactUseCaseTest {
    private val repository: IEmergencyRepository = mockk()
    private val useCase = GetContactUseCase(repository)

    @Test
    fun `on invoke repository gets address`(): Unit = runBlocking {
        coEvery { repository.getEmergencyContact() }.coAnswers { mockk() }
        useCase()
        coVerify { repository.getEmergencyContact() }
    }
}