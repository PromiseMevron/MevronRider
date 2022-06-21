package com.mevron.rides.rider.home.select_ride

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mevron.rides.rider.home.model.cars.GetCarRequests
import com.mevron.rides.rider.home.select_ride.model.GetCarsCategory2
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.remote.HTTPErrorHandler
import com.mevron.rides.rider.remote.MevronRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SelectRideViewModel @Inject constructor (private val repository: MevronRepo) : ViewModel() {
    //getCars GetCarRequests GetCarsCategory

    fun getCars(data: GetCarRequests): LiveData<GenericStatus<GetCarsCategory2>> {
        val result = MutableLiveData<GenericStatus<GetCarsCategory2>>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.getCars(data)
                if (response.isSuccessful)
                    result.postValue(GenericStatus.Success(response.body()))
                else
                    result.postValue(
                        GenericStatus.Error(
                            HTTPErrorHandler.handleErrorWithCode(
                                response
                            )
                        )
                    )
            } catch (ex: Exception) {
                ex.printStackTrace()
                result.postValue(GenericStatus.Error(HTTPErrorHandler.httpFailWithCode(ex)))
            }
        }
        return result
    }
}