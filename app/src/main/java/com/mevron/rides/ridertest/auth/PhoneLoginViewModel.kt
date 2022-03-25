package com.mevron.rides.ridertest.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mevron.rides.ridertest.auth.model.register.RegisterBody
import com.mevron.rides.ridertest.auth.model.register.RegisterResponse
import com.mevron.rides.ridertest.remote.GenericStatus
import com.mevron.rides.ridertest.remote.HTTPErrorHandler
import com.mevron.rides.ridertest.remote.MevronRepo
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