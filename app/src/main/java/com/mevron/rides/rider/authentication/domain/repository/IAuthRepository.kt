package com.mevron.rides.rider.authentication.domain.repository

import com.mevron.rides.rider.authentication.data.models.registerphone.RegisterBody
import com.mevron.rides.rider.authentication.data.models.verifyotp.ValidateOTPRequest
import com.mevron.rides.rider.authentication.data.models.createaccount.SaveDetailsRequest
import com.mevron.rides.rider.domain.DomainModel

interface IAuthRepository {
    suspend fun registerPhone(registerPhoneRequest: RegisterBody): DomainModel
    suspend fun verifyOTP(verifyOTPRequest: ValidateOTPRequest): DomainModel
    suspend fun createAccount(createAccountRequest: SaveDetailsRequest): DomainModel
}