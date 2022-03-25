package com.mevron.rides.ridertest.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.mevron.rides.ridertest.App
import com.mevron.rides.ridertest.auth.model.details.SaveDetailsRequest
import com.mevron.rides.ridertest.auth.model.details.SaveResponse
import com.mevron.rides.ridertest.remote.GenericStatus
import com.mevron.rides.ridertest.remote.HTTPErrorHandler
import com.mevron.rides.ridertest.remote.MevronRepo
import com.mevron.rides.ridertest.remote.model.getprofile.GetProfileResponse
import com.mevron.rides.ridertest.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor (private val repository: MevronRepo) : ViewModel() {


    fun saveProfile(data: SaveDetailsRequest): LiveData<GenericStatus<SaveResponse>> {

        val result = MutableLiveData<GenericStatus<SaveResponse>>()

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = repository.updateProfile(data)
                if(response.isSuccessful){
                    result.postValue(GenericStatus.Success(response.body()))
                }
                else{
                    result.postValue(GenericStatus.Error(HTTPErrorHandler.handleErrorWithCode(response)))
                }
            }catch (ex: Exception){
                ex.printStackTrace()
                result.postValue(GenericStatus.Error(HTTPErrorHandler.httpFailWithCode(ex)))
            }
        }
        return result
    }


    fun getProfile(): LiveData<GenericStatus<GetProfileResponse>> {

        val result = MutableLiveData<GenericStatus<GetProfileResponse>>()

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = repository.getProfile()
                if(response.isSuccessful){
                    val sPref= App.ApplicationContext.getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE)
                    val editor = sPref.edit()
                    val gson = Gson()
                    val user = gson.toJson(response.body()?.success?.data)
                    editor.putString(Constants.PROFILE, user)
                    editor.apply()
                  //  result.postValue(GenericStatus.Success(response.body()))
                    result.postValue(GenericStatus.Success(response.body()))
                }
                else{
                    result.postValue(GenericStatus.Error(HTTPErrorHandler.handleErrorWithCode(response)))
                }
            }catch (ex: Exception){
                ex.printStackTrace()
                result.postValue(GenericStatus.Error(HTTPErrorHandler.httpFailWithCode(ex)))
            }
        }
        return result
    }

    fun resendLink(): LiveData<GenericStatus<SaveResponse>> {

        val result = MutableLiveData<GenericStatus<SaveResponse>>()

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = repository.resendEmailVerify()
                if(response.isSuccessful){

                    result.postValue(GenericStatus.Success(response.body()))
                }
                else{
                    result.postValue(GenericStatus.Error(HTTPErrorHandler.handleErrorWithCode(response)))
                }
            }catch (ex: Exception){
                ex.printStackTrace()
                result.postValue(GenericStatus.Error(HTTPErrorHandler.httpFailWithCode(ex)))
            }
        }
        return result
    }

}