package com.mevron.rides.rider.savedplaces.domain.usecase

import com.mevron.rides.rider.savedplaces.data.model.UpdateAddress
import com.mevron.rides.rider.savedplaces.domain.repository.IAddressRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class UpdateAddressUseCaseTest {
    private val repository: IAddressRepository = mockk()
    private val useCase = UpdateAddressUseCase(repository)

    @Test
    fun `on invoke repository gets address`(): Unit = runBlocking {
        coEvery { repository.updateAnAddress(any(), any()) }.coAnswers { mockk() }
        val request = UpdateAddress(
            address = "LL",
            latitude = "ss",
            longitude = "ss",
            name = "oo",
            type = "sss"
        )
        useCase("aa", request)
        coVerify { repository.updateAnAddress("aa", request) }
    }
}