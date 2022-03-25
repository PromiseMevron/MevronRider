package com.mevron.rides.ridertest.home.select_ride

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mevron.rides.ridertest.home.model.cars.GetCarRequests
import com.mevron.rides.ridertest.home.model.cars.GetCarsCategory
import com.mevron.rides.ridertest.remote.GenericStatus
import com.mevron.rides.ridertest.remote.HTTPErrorHandler
import com.mevron.rides.ridertest.remote.MevronRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SelectRideViewModel @Inject constructor (private val repository: MevronRepo) : ViewModel() {
    //getCars GetCarRequests GetCarsCategory


    fun getCars(data: GetCarRequests): LiveData<GenericStatus<GetCarsCategory>> {
        val result = MutableLiveData<GenericStatus<GetCarsCategory>>()

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