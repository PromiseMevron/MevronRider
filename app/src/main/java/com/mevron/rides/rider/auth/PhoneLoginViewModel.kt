package com.mevron.rides.rider.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mevron.rides.rider.authentication.data.models.registerphone.RegisterBody
import com.mevron.rides.rider.auth.model.register.RegisterResponse
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.remote.HTTPErrorHandler
import com.mevron.rides.rider.remote.MevronRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneLoginViewModel @Inject constructor (private val repository: MevronRepo) : ViewModel() {

    fun phoneLogin(data: RegisterBody): LiveData<GenericStatus<RegisterResponse>> {

          val result = MutableLiveData<GenericStatus<RegisterResponse>>()

   CoroutineScope(Dispatchers.IO).launch {
       try{
           val response = repository.registerPhone(data)
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