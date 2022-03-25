package com.mevron.rides.ridertest.home.ride

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mevron.rides.ridertest.home.ride.model.ConfirmRideResponse
import com.mevron.rides.ridertest.remote.GenericStatus
import com.mevron.rides.ridertest.remote.HTTPErrorHandler
import com.mevron.rides.ridertest.remote.MevronRepo
import com.mevron.rides.ridertest.remote.model.RideRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ConfirmRideViewModel @Inject constructor (private val repository: MevronRepo) : ViewModel() {


    fun confirmRider(data: RideRequest): LiveData<GenericStatus<ConfirmRideResponse>> {

        val result = MutableLiveData<GenericStatus<ConfirmRideResponse>>()

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = repository.createRide(data)
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