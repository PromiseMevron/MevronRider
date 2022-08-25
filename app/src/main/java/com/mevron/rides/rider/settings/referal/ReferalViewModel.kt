package com.mevron.rides.rider.settings.referal

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mevron.rides.rider.App
import com.mevron.rides.rider.localdb.ReferalDetail
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.remote.HTTPErrorHandler
import com.mevron.rides.rider.remote.MevronRepo
import com.mevron.rides.rider.remote.model.GeneralResponse
import com.mevron.rides.rider.settings.referal.model.GetReferalHistory
import com.mevron.rides.rider.settings.referal.model.SetReferal
import com.mevron.rides.rider.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReferalViewModel @Inject constructor (private val repository: MevronRepo) : ViewModel() {

    fun getReferal(): LiveData<GenericStatus<GetReferalHistory>> {

        val result = MutableLiveData<GenericStatus<GetReferalHistory>>()

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = repository.getReferralHistory()
                if(response.isSuccessful){
                    result.postValue(GenericStatus.Success(response.body()))
                    val sPref= App.ApplicationContext.getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE)
                    val editor = sPref.edit()
                    editor.putString(Constants.REFERRAL, response.body()?.success?.data?.referralCode.toString())
                    //referralStatus
                    editor.putInt(Constants.REFERRAL_STATUS, response.body()?.success?.data?.referralStatus ?: 0)
                    editor.apply()
                    val dt = response.body()?.success?.data?.result
                    if (dt?.isNotEmpty() == true){
                       // repository.deleteAllRefer()
                        for (d in dt){
                            val add = ReferalDetail(createAt = d.createAt, id = d.id,
                                title = d.title, description = d.description, category = d.category
                            )
                            repository.saveReferInDb(add)
                        }
                    }
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



    fun addReferal(data: SetReferal): LiveData<GenericStatus<GeneralResponse>> {

        val result = MutableLiveData<GenericStatus<GeneralResponse>>()

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = repository.setReferral(data)
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

 /*   fun getReferalFromDB(): LiveData<MutableList<ReferalDetail>> {
      //  return repository.getllReferal()
    }*/

}