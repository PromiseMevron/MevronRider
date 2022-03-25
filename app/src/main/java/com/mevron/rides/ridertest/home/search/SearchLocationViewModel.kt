package com.mevron.rides.ridertest.home.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mevron.rides.ridertest.home.model.getAddress.GetSavedAddresss
import com.mevron.rides.ridertest.localdb.SavedAddress
import com.mevron.rides.ridertest.remote.GenericStatus
import com.mevron.rides.ridertest.remote.HTTPErrorHandler
import com.mevron.rides.ridertest.remote.MevronRepo
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