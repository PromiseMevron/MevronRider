package com.mevron.rides.rider.domain.usecase

import com.mevron.rides.rider.domain.IOrderPropertiesRepository
import javax.inject.Inject

class SetOrderPropertiesUseCase @Inject constructor(
    private val repository: IOrderPropertiesRepository
) {
    operator fun invoke(key: String, value: String) = repository.setSelectedProperty(key, value)
}