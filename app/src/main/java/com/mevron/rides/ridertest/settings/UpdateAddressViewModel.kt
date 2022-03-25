package com.mevron.rides.ridertest.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mevron.rides.ridertest.remote.MevronRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import com.mevron.rides.ridertest.home.model.getAddress.UpdateAddress
import com.mevron.rides.ridertest.remote.GenericStatus
import com.mevron.rides.ridertest.remote.HTTPErrorHandler
import com.mevron.rides.ridertest.remote.model.GeneralResponse
import com.mevron.rides.ridertest.localdb.SavedAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UpdateAddressViewModel @Inject constructor (private val repository: MevronRepo) : ViewModel() {

    fun updateAddresses(add: UpdateAddress, path: String): LiveData<GenericStatus<GeneralResponse>> {

        val result = MutableLiveData<GenericStatus<GeneralResponse>>()

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = repository.updataAdd(path, add)
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

    fun updateInDb(add: SavedAddress){
        CoroutineScope(Dispatchers.IO).launch {
            repository.updataAddInDB(add)
        }
    }

}