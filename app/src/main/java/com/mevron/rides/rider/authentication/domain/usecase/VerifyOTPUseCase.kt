package com.mevron.rides.rider.authentication.domain.usecase

import com.mevron.rides.rider.authentication.data.models.verifyotp.ValidateOTPRequest
import com.mevron.rides.rider.authentication.domain.repository.IAuthRepository
import javax.inject.Inject

class VerifyOTPUseCase @Inject constructor(private val repository: IAuthRepository) {
    suspend operator fun invoke(verifyOTP: ValidateOTPRequest) =
        repository.verifyOTP(verifyOTP)
}