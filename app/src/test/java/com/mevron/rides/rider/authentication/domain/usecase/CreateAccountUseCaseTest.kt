package com.mevron.rides.rider.authentication.domain.usecase

import com.mevron.rides.rider.authentication.data.models.createaccount.SaveDetailsRequest
import com.mevron.rides.rider.authentication.domain.repository.IAuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class CreateAccountUseCaseTest {

    private val repo: IAuthRepository = mockk()
    private val userCase = CreateAccountUseCase(repo)

    @Test
    fun `on invoke repository creates account`(): Unit = runBlocking {
        coEvery { repo.createAccount(any()) }.coAnswers { mockk() }
        val request: SaveDetailsRequest = mockk()
        userCase(request)
        coVerify { repo.createAccount(request) }
    }
}