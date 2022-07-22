package com.mevron.rides.rider.domain

interface IOrderPropertiesRepository {
    fun setSelectedProperty(key: String, value: String)
    fun getSelectedProperty(key: String): String
}