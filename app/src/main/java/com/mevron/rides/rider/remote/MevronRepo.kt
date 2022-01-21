package com.mevron.rides.rider.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mevron.rides.rider.auth.model.details.SaveDetailsRequest
import com.mevron.rides.rider.auth.model.details.SaveResponse
import com.mevron.rides.rider.auth.model.otp.OTPResponse
import com.mevron.rides.rider.auth.model.otp.ValidateOTPRequest
import com.mevron.rides.rider.auth.model.register.RegisterBody
import com.mevron.rides.rider.auth.model.register.RegisterResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

class MevronRepo @Inject constructor (private val api: MevronAPI) {



    suspend fun registerPhone(data: RegisterBody): Response<RegisterResponse> {
        return api.registerPhone(data)

    }

    suspend fun validateOTP(data: ValidateOTPRequest): Response<OTPResponse> {
        return api.verifyOTP(data)

    }

    suspend fun sendDetail(data: SaveDetailsRequest): Response<SaveResponse> {
        return api.sendDetail(data)

    }
}