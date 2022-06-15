package com.mevron.rides.rider.authentication.data.repository

import com.mevron.rides.rider.authentication.data.models.createaccount.SaveDetailsRequest
import com.mevron.rides.rider.authentication.data.models.registerphone.RegisterBody
import com.mevron.rides.rider.authentication.data.models.registerphone.RegisterData
import com.mevron.rides.rider.authentication.data.models.registerphone.RegisterPhoneResponse
import com.mevron.rides.rider.authentication.data.models.registerphone.RegisterPhoneSuccess
import com.mevron.rides.rider.authentication.data.models.verifyotp.OTPData
import com.mevron.rides.rider.authentication.data.models.verifyotp.ValidateOTPRequest
import com.mevron.rides.rider.authentication.data.models.verifyotp.ValidateOTPResponse
import com.mevron.rides.rider.authentication.data.models.verifyotp.ValidateOTPSuccess
import com.mevron.rides.rider.authentication.data.network.AuthApi
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.mockError
import com.mevron.rides.rider.remote.model.GeneralResponse
import com.mevron.rides.rider.remote.model.Success
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import retrofit2.Response


class AuthRepositoryTest{

    private val authApi: AuthApi = mockk()
    private val repository = AuthRepository(authApi)

    @Test
    fun `when AuthRepository#registerPhone is invoked - sends api request to register phone`(): Unit =
        runBlocking {
            val registerPhoneResponseBody = RegisterPhoneResponse(
                success = RegisterPhoneSuccess(
                    registerdata = RegisterData(
                        code = "3333",
                        expireAt = "now"
                    ), message = "testMessage", status = "testStatus"
                )
            )
            val response: Response<RegisterPhoneResponse> =
                Response.success(registerPhoneResponseBody)
            coEvery { authApi.registerPhone(any()) }.coAnswers { response }
           // val phoneRequest = RegisterPhoneRequest(country = "test", phoneNumber = "123")
            val phoneRequest = RegisterBody(country = "test", phoneNumber = "123")
            val result = repository.registerPhone(phoneRequest)

            coVerify { authApi.registerPhone(phoneRequest) }
            assert(result is DomainModel.Success)
        }

    @Test
    fun `when AuthApi#registerPhone fails - repository returns error`() = runBlocking {
        val response: Response<RegisterPhoneResponse> = mockError()
        coEvery { authApi.registerPhone(any()) }.coAnswers { response }
        val request: RegisterBody = mockk()

        val result = repository.registerPhone(request)

        assert(result is DomainModel.Error)
    }

    @Test
    fun `when AuthRepository#verifyOTP is invoked - sends api request to verifyOTP`(): Unit =
        runBlocking {
            val response: Response<ValidateOTPResponse> = mockk {
                every { isSuccessful }.returns(true)
                every { body() }.returns(
                    ValidateOTPResponse(
                        ValidateOTPSuccess(
                            otpData = OTPData(
                                accessToken = "testToken",
                                riderType = "taxi",
                                uuid = "testUUID",
                                stage = ""
                            ),
                            message = "test",
                            status = "SUCCESS"
                        )
                    )
                )
            }

            coEvery { authApi.verifyOTP(any()) }.coAnswers { response }
            val otpRequest = ValidateOTPRequest(code = "233", phoneNumber = "123")

            repository.verifyOTP(otpRequest)

            coVerify { authApi.verifyOTP(otpRequest) }
        }

    @Test
    fun `when AuthApi#verifyOTP fails - repository returns error`(): Unit =
        runBlocking {
            val response: Response<ValidateOTPResponse> = mockError()
            coEvery { authApi.verifyOTP(any()) }.coAnswers { response }
            val request: ValidateOTPRequest = mockk()

            val result = repository.verifyOTP(request)

            assert(result is DomainModel.Error)
        }

    @Test
    fun `when AuthRepository#createAccount is invoked - sends api to createAccount`(): Unit =
        runBlocking {
            val response: Response<GeneralResponse> =
                Response.success(GeneralResponse(
                    Success("message", "200")))
            coEvery { authApi.sendDetail(any()) }.coAnswers { response }
            val createAccountRequest = SaveDetailsRequest(
                email = "testEmail",
                firstName = "testFirstName",
                lastName = "testLastName",
            )

            val result = repository.createAccount(createAccountRequest)

            coVerify { authApi.sendDetail(createAccountRequest) }
            assert(result is DomainModel.Success)
        }

    @Test
    fun `when AuthApi#createAccount fails - repository returns error`(): Unit =
        runBlocking {
            val response: Response<GeneralResponse> = mockError()
            coEvery { authApi.sendDetail(any()) }.coAnswers { response }
            val request: SaveDetailsRequest = mockk()

            val result = repository.createAccount(request)

            assert(result is DomainModel.Error)
        }
}