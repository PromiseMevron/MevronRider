package com.mevron.rides.rider.emerg.domain.usecase

import com.mevron.rides.rider.emerg.domain.repository.IEmergencyRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class DeleteContactUseCaseTest {
    private val repository: IEmergencyRepository = mockk()
    private val useCase = DeleteContactUseCase(repository)

    @Test
    fun `on invoke repository gets address`(): Unit = runBlocking {
        coEvery { repository.deleEmergencyContact(any()) }.coAnswers { mockk() }
        useCase("2")
        coVerify { repository.deleEmergencyContact("2") }
    }
}