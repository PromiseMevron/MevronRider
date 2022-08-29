package com.mevron.rides.rider.domain.usecase

import com.mevron.rides.rider.domain.IDriverLocationRepository
import javax.inject.Inject


class GetDriverLocationUseCase @Inject constructor(
    private val repository: IDriverLocationRepository
) {
    operator fun invoke() = repository.currentDriverState
}