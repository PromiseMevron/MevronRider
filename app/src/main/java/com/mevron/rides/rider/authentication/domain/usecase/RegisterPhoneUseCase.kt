package com.mevron.rides.rider.authentication.domain.usecase

import com.mevron.rides.rider.authentication.data.models.registerphone.RegisterBody
import com.mevron.rides.rider.authentication.domain.repository.IAuthRepository
import javax.inject.Inject

class RegisterPhoneUseCase @Inject constructor(private val repository: IAuthRepository) {
    suspend operator fun invoke(registerPhone: RegisterBody) =
        repository.registerPhone(registerPhone)
}