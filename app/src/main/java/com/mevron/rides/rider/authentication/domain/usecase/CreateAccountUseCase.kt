package com.mevron.rides.rider.authentication.domain.usecase

import com.mevron.rides.rider.authentication.data.models.createaccount.SaveDetailsRequest
import com.mevron.rides.rider.authentication.domain.repository.IAuthRepository
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(private val repository: IAuthRepository) {
    suspend operator fun invoke(saveDetailsRequest: SaveDetailsRequest) =
        repository.createAccount(saveDetailsRequest)
}