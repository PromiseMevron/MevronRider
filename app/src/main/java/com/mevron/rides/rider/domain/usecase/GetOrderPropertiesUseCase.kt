package com.mevron.rides.rider.domain.usecase

import com.mevron.rides.rider.domain.IOrderPropertiesRepository
import javax.inject.Inject

class GetOrderPropertiesUseCase @Inject constructor(
    private val repository: IOrderPropertiesRepository
) {
    operator fun invoke(key: String) = repository.getSelectedProperty(key)
}