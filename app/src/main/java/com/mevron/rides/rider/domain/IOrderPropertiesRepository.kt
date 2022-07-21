package com.mevron.rides.rider.domain

import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.home.model.PaymentModel

interface IOrderPropertiesRepository {
    fun setSelectedCar(car: String)
    fun setSelectedPayMethod(pay: PaymentModel)
    fun setSelectedPickup(data: LocationModel)
    fun setSelectedDropOff(data: LocationModel)
    fun getSelectedCar(): String
    fun getSelectedPayMethod(): PaymentModel
    fun getSelectedPickup(): LocationModel
    fun getSelectedDropOff(): LocationModel
}