package com.mevron.rides.rider.authentication.data.repository

import com.mevron.rides.rider.authentication.data.models.registerphone.RegisterBody
import com.mevron.rides.rider.authentication.data.models.registerphone.RegisterPhoneResponse
import com.mevron.rides.rider.authentication.data.models.verifyotp.ValidateOTPRequest
import com.mevron.rides.rider.authentication.data.models.verifyotp.ValidateOTPResponse
import com.mevron.rides.rider.authentication.data.network.AuthApi
import com.mevron.rides.rider.authentication.domain.model.CreateAccountDomainModel
import com.mevron.rides.rider.authentication.domain.model.RegisterPhoneDomainData
import com.mevron.rides.rider.authentication.domain.model.VerifyOTPDomainModel
import com.mevron.rides.rider.authentication.domain.repository.IAuthRepository
import com.mevron.rides.rider.authentication.data.models.createaccount.SaveDetailsRequest
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.remote.model.GeneralResponse

class AuthRepository(private val authApi: AuthApi) : IAuthRepository {
    override suspend fun registerPhone(registerPhoneRequest: RegisterBody) =
        authApi.registerPhone(registerPhoneRequest).let {
            if (it.isSuccessful) {
                it.body()?.toDomainModel() ?: DomainModel.Error(Throwable("Empty result found"))
            } else {
                DomainModel.Error(Throwable(it.errorBody().toString()))
            }
        }

    override suspend fun verifyOTP(verifyOTPRequest: ValidateOTPRequest) =
        authApi.verifyOTP(verifyOTPRequest).let {
            if (it.isSuccessful) {
                it.body()?.toDomainModel() ?: DomainModel.Error(Throwable("Empty result found"))
            } else {
                DomainModel.Error(Throwable(it.errorBody().toString()))
            }
        }

    override suspend fun createAccount(createAccountRequest: SaveDetailsRequest) =
        authApi.sendDetail(createAccountRequest).let {
            if (it.isSuccessful) {
                it.body()?.toDomainModel() ?: DomainModel.Error(Throwable("Empty result found"))
            } else {
                DomainModel.Error(Throwable(it.errorBody().toString()))
            }
        }


    private fun RegisterPhoneResponse.toDomainModel() =
        DomainModel.Success(
            data = RegisterPhoneDomainData(
                phoneCodeData = this.success.registerdata,
                message = this.success.message,
                status = this.success.status
            )
        )

    private fun ValidateOTPResponse.toDomainModel() = DomainModel.Success(
        data = VerifyOTPDomainModel(
            accessToken = this.success.otpData.accessToken,
            riderType = this.success.otpData.riderType,
            uuid = this.success.otpData.uuid
        )
    )

    private fun GeneralResponse.toDomainModel() = DomainModel.Success(
        data = CreateAccountDomainModel(
            message = this.success.message,
            status = this.success.status
        )
    )

}