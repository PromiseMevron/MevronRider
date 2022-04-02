package com.mevron.rides.ridertest.remote

import androidx.lifecycle.LiveData
import com.mevron.rides.ridertest.auth.model.details.SaveDetailsRequest
import com.mevron.rides.ridertest.auth.model.details.SaveResponse
import com.mevron.rides.ridertest.auth.model.otp.OTPResponse
import com.mevron.rides.ridertest.auth.model.otp.ValidateOTPRequest
import com.mevron.rides.ridertest.auth.model.register.RegisterBody
import com.mevron.rides.ridertest.auth.model.register.RegisterResponse
import com.mevron.rides.ridertest.home.model.AddCard
import com.mevron.rides.ridertest.home.model.cars.GetCarRequests
import com.mevron.rides.ridertest.home.model.cars.GetCarsCategory
import com.mevron.rides.ridertest.home.model.getAddress.GetSavedAddresss
import com.mevron.rides.ridertest.home.model.getAddress.SaveAddressRequest
import com.mevron.rides.ridertest.home.model.getAddress.UpdateAddress
import com.mevron.rides.ridertest.home.model.getCard.GetCardResponse
import com.mevron.rides.ridertest.home.ride.model.ConfirmRideResponse
import com.mevron.rides.ridertest.home.select_ride.model.GetCarsCategory2
import com.mevron.rides.ridertest.localdb.MevronDao
import com.mevron.rides.ridertest.localdb.ReferalDetail
import com.mevron.rides.ridertest.localdb.SavedAddress
import com.mevron.rides.ridertest.remote.model.GeneralResponse
import com.mevron.rides.ridertest.remote.model.RideRequest
import com.mevron.rides.ridertest.remote.model.getprofile.GetProfileResponse
import com.mevron.rides.ridertest.settings.emerg.AddContactRequest
import com.mevron.rides.ridertest.settings.emerg.model.GetContactsResponse
import com.mevron.rides.ridertest.settings.emerg.model.UpdateEmergencyContact
import com.mevron.rides.ridertest.settings.referal.model.GetReferalHistory
import com.mevron.rides.ridertest.settings.referal.model.ReferalReport
import com.mevron.rides.ridertest.settings.referal.model.SetReferal
import com.mevron.rides.ridertest.settings.referal.model.notif.NotificationResponse
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

    suspend fun getCars(data: GetCarRequests): Response<GetCarsCategory2> {
        return api.getCars(data)
    }

    suspend fun saveAddressInDb(add: SavedAddress) {
        dao.insert(add)
    }

    suspend fun deleteAllAdd() {
        dao.deleteAllAddress()
    }

    suspend fun updataAddInDB(add: SavedAddress) {
        dao.update(add)
    }

    suspend fun updataAdd(path: String, add: UpdateAddress): Response<GeneralResponse> {
        return api.updateAddress(path, add)
    }

    fun getllAddress(): LiveData<MutableList<SavedAddress>> {
        return dao.getAllAddress()
    }

    suspend fun addCard(data: AddCard): Response<GeneralResponse> {
        return api.addCard(data)
    }

    suspend fun getCards(): Response<GetCardResponse> {
        return api.getCards()
    }

    suspend fun createRide(data: RideRequest): Response<ConfirmRideResponse> {
        return api.makeARideRequest(data)
    }

    suspend fun deleteCard(id: String): Response<GeneralResponse> {
        return api.deleteCard(id)
    }

    suspend fun getProfile(): Response<GetProfileResponse> {
        return api.getProfile()
    }

    suspend fun updateProfile(data: SaveDetailsRequest): Response<SaveResponse> {
        return api.updateProfile(data)
    }

    suspend fun resendEmailVerify(): Response<SaveResponse> {
        return api.resendEmailLink()
    }

    suspend fun getReferralHistory(): Response<GetReferalHistory> {
        return api.getReferralHistory()
    }

    suspend fun setReferral(data: SetReferal): Response<GeneralResponse> {
        return api.setReferral(data)
    }

    suspend fun getReferralReport(data: ReferalReport): Response<GeneralResponse> {
        return api.getReferralReport(data = data)
    }

    suspend fun saveReferInDb(add: ReferalDetail) {
        dao.insertRefer(add)
    }

    suspend fun deleteAllRefer() {
        dao.deleteAllReferral()
    }


    fun getllReferal(): LiveData<MutableList<ReferalDetail>> {
        return dao.getAllReferal()
    }

    suspend fun saveEmergency(data: AddContactRequest): Response<GeneralResponse> {
        return api.saveEmergency(data)
    }

    suspend fun updateEmergency(data: UpdateEmergencyContact, id: String): Response<GeneralResponse> {
        return api.updateEmergency(id = id, data = data)
    }

    suspend fun deleteEmergency(id: String): Response<GeneralResponse> {
        return api.deleteEmergency(id = id)
    }

    suspend fun getEmergency(): Response<GetContactsResponse> {
        return api.getEmergency()
    }

    suspend fun getPromo(): Response<GeneralResponse> {
        return api.getPromo()
    }


    suspend fun getNotification(): Response<NotificationResponse> {
        return api.getNotifications()
    }


}