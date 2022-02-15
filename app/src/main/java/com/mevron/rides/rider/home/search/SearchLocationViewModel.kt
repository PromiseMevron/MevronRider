package com.mevron.rides.rider.home.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mevron.rides.rider.home.model.getAddress.GetSavedAddresss
import com.mevron.rides.rider.localdb.SavedAddress
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.remote.HTTPErrorHandler
import com.mevron.rides.rider.remote.MevronRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchLocationViewModel @Inject constructor (private val repository: MevronRepo) : ViewModel() {

    fun getAddresses(): LiveData<GenericStatus<GetSavedAddresss>> {

        val result = MutableLiveData<GenericStatus<GetSavedAddresss>>()

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = repository.getAddress()
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

    fun getAddressFromDB(): LiveData<MutableList<SavedAddress>> {
        return repository.getllAddress()
    }
}