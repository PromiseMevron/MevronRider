package com.mevron.rides.rider.home.selectmap

import android.util.Log
import androidx.lifecycle.ViewModel
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.sharedprefrence.domain.usescases.SetPreferenceUseCase
import com.mevron.rides.rider.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectOnMapViewModel @Inject constructor(private val setPreferenceUseCase: SetPreferenceUseCase) : ViewModel() {

    fun updateOrderStatus(model: Array<LocationModel>){
        Log.d("DIRECTION", model[0].lat.toString())
        setPreferenceUseCase(Constants.PICK_UP_ADD, model[0].address)
        setPreferenceUseCase(Constants.DROP_OFF_ADD, model[1].address)
        setPreferenceUseCase(Constants.PICK_UP_LAT, model[0].lat.toString())
        setPreferenceUseCase(Constants.DROP_OFF_LAT, model[1].lat.toString())
        setPreferenceUseCase(Constants.PICK_UP_LNG, model[0].lng.toString())
        setPreferenceUseCase(Constants.DROP_OFF_LNG, model[1].lng.toString())
    }
}