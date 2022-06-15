package com.mevron.rides.rider.sharedprefrence.domain.usescases

import com.mevron.rides.rider.sharedprefrence.domain.repository.IPreferenceRepository
import javax.inject.Inject

class SetPreferenceUseCase @Inject constructor(private val repository: IPreferenceRepository) {

    operator fun invoke(key: String, value: String) =
        repository.setStringForKey(key = key, value = value)
}