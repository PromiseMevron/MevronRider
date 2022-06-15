package com.mevron.rides.rider.authentication.domain.usecase

import com.mevron.rides.rider.authentication.data.models.registerphone.RegisterBody
import com.mevron.rides.rider.authentication.domain.repository.IAuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test


class RegisterPhoneUseCaseTest {

    private val repo: IAuthRepository = mockk()
    private val userCase = RegisterPhoneUseCase(repo)

    @Test
    fun `on invoke repository creates account`(): Unit = runBlocking {
        coEvery { repo.registerPhone(any()) }.coAnswers { mockk() }
        val request: RegisterBody = mockk()
        userCase(request)
        coVerify { repo.registerPhone(request) }
    }
}