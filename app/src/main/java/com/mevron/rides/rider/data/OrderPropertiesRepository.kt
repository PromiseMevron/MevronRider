package com.mevron.rides.rider.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mevron.rides.rider.domain.IOrderPropertiesRepository
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.home.model.PaymentModel
import com.mevron.rides.rider.sharedprefrence.domain.repository.IPreferenceRepository
import com.mevron.rides.rider.util.Constants.CAR
import com.mevron.rides.rider.util.Constants.LocationModelDrop
import com.mevron.rides.rider.util.Constants.LocationModelPick
import com.mevron.rides.rider.util.Constants.ThePaymentModel
import javax.inject.Inject

class OrderPropertiesRepository @Inject constructor(private val repository: IPreferenceRepository) :
    IOrderPropertiesRepository {

    override fun setSelectedCar(car: String) {
        repository.setStringForKey(CAR, car)
    }

    override fun setSelectedPayMethod(pay: PaymentModel) {
        repository.setStringForKey(ThePaymentModel, toJsonString(pay))
    }

    override fun setSelectedPickup(data: LocationModel) {
        repository.setStringForKey(LocationModelPick, toJsonString(data))
    }

    override fun setSelectedDropOff(data: LocationModel) {
        repository.setStringForKey(LocationModelDrop, toJsonString(data))
    }

    override fun getSelectedCar(): String = repository.getStringForKey(CAR)

    override fun getSelectedPayMethod(): PaymentModel {
        val json = toDataClass(ThePaymentModel)
        val gson = Gson()
        return gson.fromJson(json, PaymentModel::class.java)
    }

    override fun getSelectedPickup(): LocationModel {
        val json = toDataClass(LocationModelPick)
        val gson = Gson()
        return gson.fromJson(json, LocationModel::class.java)
    }

    override fun getSelectedDropOff(): LocationModel {
        val json = toDataClass(LocationModelDrop)
        val gson = Gson()
        return gson.fromJson(json, LocationModel::class.java)
    }

    private fun <T> toJsonString(t: T): String {
        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        return gsonPretty.toJson(t)
    }

    private fun toDataClass(key: String): String {
        return repository.getStringForKey(key)
    }
}