package com.mevron.rides.ridertest.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mevron.rides.ridertest.auth.model.otp.OTPResponse
import com.mevron.rides.ridertest.auth.model.otp.ValidateOTPRequest
import com.mevron.rides.ridertest.remote.GenericStatus
import com.mevron.rides.ridertest.remote.HTTPErrorHandler
import com.mevron.rides.ridertest.remote.MevronRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OTViewModel @Inject constructor (private val repository: MevronRepo) : ViewModel() {

    fun validateOTP(data: ValidateOTPRequest): LiveData<GenericStatus<OTPResponse>> {

        val result = MutableLiveData<GenericStatus<OTPResponse>>()

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = repository.validateOTP(data)
                if(response.isSuccessful)
                    result.postValue(GenericStatus.Success(response.body()))
                else
                    result.postValue(GenericStatus.Error(HTTPErrorHandler.handleErrorWithCode(response)))
            }catch (ex: Exception){
                ex.printStackTrace()
                result.postValue(GenericStatus.Error(HTTPErrorHandler.httpFailWithCode(ex)))
            }
        }
        return result

    }
}