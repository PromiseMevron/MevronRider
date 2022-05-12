package com.mevron.rides.rider.supportpages.domain.usecase

import com.mevron.rides.rider.supportpages.domain.repository.ISupportRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class GetPromoUseCaseTest {

    private val repository: ISupportRepository = mockk()
    private val useCase = GetPromoUseCase(repository)

    @Test
    fun `on invoke repository gets address`(): Unit = runBlocking {
        coEvery { repository.getPromos() }.coAnswers { mockk() }
        useCase()
        coVerify { repository.getPromos() }
    }
}