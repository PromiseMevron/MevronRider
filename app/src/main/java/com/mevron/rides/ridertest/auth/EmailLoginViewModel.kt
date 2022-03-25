package com.mevron.rides.ridertest.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mevron.rides.ridertest.auth.model.details.SaveDetailsRequest
import com.mevron.rides.ridertest.auth.model.details.SaveResponse
import com.mevron.rides.ridertest.remote.GenericStatus
import com.mevron.rides.ridertest.remote.HTTPErrorHandler
import com.mevron.rides.ridertest.remote.MevronRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailLoginViewModel @Inject constructor (private val repository: MevronRepo) : ViewModel() {

    fun sendDetail(data: SaveDetailsRequest): LiveData<GenericStatus<SaveResponse>> {

        val result = MutableLiveData<GenericStatus<SaveResponse>>()

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = repository.sendDetail(data)
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