package com.mevron.rides.rider.sharedprefrence.domain.repository

interface IPreferenceRepository {
    fun getStringForKey(key: String): String
    fun setStringForKey(key: String, value: String)
    fun clear()
}