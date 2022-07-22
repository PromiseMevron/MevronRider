package com.mevron.rides.rider.data

import com.mevron.rides.rider.domain.IOrderPropertiesRepository
import com.mevron.rides.rider.sharedprefrence.domain.repository.IPreferenceRepository
import javax.inject.Inject

class OrderPropertiesRepository @Inject constructor(private val repository: IPreferenceRepository) :
    IOrderPropertiesRepository {

    override fun setSelectedProperty(key: String, value: String) {
        repository.setStringForKey(key, value)
    }

    override fun getSelectedProperty(key: String): String = repository.getStringForKey(key)


}