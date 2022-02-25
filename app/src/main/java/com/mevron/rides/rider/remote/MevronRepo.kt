package com.mevron.rides.rider.remote

import androidx.lifecycle.LiveData
import com.mevron.rides.rider.auth.model.details.SaveDetailsRequest
import com.mevron.rides.rider.auth.model.details.SaveResponse
import com.mevron.rides.rider.auth.model.otp.OTPResponse
import com.mevron.rides.rider.auth.model.otp.ValidateOTPRequest
import com.mevron.rides.rider.auth.model.register.RegisterBody
import com.mevron.rides.rider.auth.model.register.RegisterResponse
import com.mevron.rides.rider.home.model.AddCard
import com.mevron.rides.rider.home.model.cars.GetCarRequests
import com.mevron.rides.rider.home.model.cars.GetCarsCategory
import com.mevron.rides.rider.home.model.getAddress.GetSavedAddresss
import com.mevron.rides.rider.home.model.getAddress.SaveAddressRequest
import com.mevron.rides.rider.home.model.getAddress.UpdateAddress
import com.mevron.rides.rider.home.model.getCard.GetCardResponse
import com.mevron.rides.rider.localdb.MevronDao
import com.mevron.rides.rider.localdb.SavedAddress
import com.mevron.rides.rider.remote.model.GeneralResponse
import retrofit2.Response
import javax.inject.Inject

class MevronRepo @Inject constructor (private val api: MevronAPI, private val dao: MevronDao) {



    suspend fun registerPhone(data: RegisterBody): Response<RegisterResponse> {
        return api.registerPhone(data)
    }

    suspend fun validateOTP(data: ValidateOTPRequest): Response<OTPResponse> {
        return api.verifyOTP(data)
    }

    suspend fun sendDetail(data: SaveDetailsRequest): Response<SaveResponse> {
        return api.sendDetail(data)
    }


    suspend fun saveAddress(data: SaveAddressRequest): Response<GeneralResponse> {
        return api.saveAddress(data)
    }

    suspend fun getAddress(): Response<GetSavedAddresss> {
        return api.getAddress()
    }

    suspend fun getCars(data: GetCarRequests): Response<GetCarsCategory> {
        return api.getCars(data)
    }

    suspend fun saveAddressInDb(add: SavedAddress){
        dao.insert(add)
    }

    suspend fun deleteAllAdd(){
        dao.deleteAllAddress()
    }

    suspend fun updataAddInDB(add: SavedAddress){
        dao.update(add)
    }

    suspend fun updataAdd(path: String, add: UpdateAddress): Response<GeneralResponse>{
       return api.updateAddress(path, add)
    }

    fun getllAddress(): LiveData<MutableList<SavedAddress>>{
        return dao.getAllAddress()
    }

    suspend fun addCard(data: AddCard): Response<GeneralResponse>{
        return api.addCard(data)
    }

    suspend fun getCards(): Response<GetCardResponse>{
        return  api.getCards()
    }
}