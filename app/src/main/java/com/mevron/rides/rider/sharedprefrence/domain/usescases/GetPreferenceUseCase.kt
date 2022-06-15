package com.mevron.rides.rider.sharedprefrence.domain.usescases

import com.mevron.rides.rider.sharedprefrence.domain.repository.IPreferenceRepository
import javax.inject.Inject

class GetPreferenceUseCase @Inject constructor(private val repository: IPreferenceRepository) {

    operator fun invoke(key: String) = repository.getStringForKey(key = key)
}