package com.mevron.rides.rider.authentication.domain.usecase

import com.mevron.rides.rider.authentication.data.models.verifyotp.ValidateOTPRequest
import com.mevron.rides.rider.authentication.domain.repository.IAuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test


class VerifyOTPUseCaseTest {

    private val repo: IAuthRepository = mockk()
    private val userCase = VerifyOTPUseCase(repo)

    @Test
    fun `on invoke repository validates otp`(): Unit = runBlocking {
        coEvery { repo.verifyOTP(any()) }.coAnswers { mockk() }
        val request: ValidateOTPRequest = mockk()
        userCase(request)
        coVerify { repo.verifyOTP(request) }
    }
}