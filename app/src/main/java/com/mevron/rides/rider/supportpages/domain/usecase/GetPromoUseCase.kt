package com.mevron.rides.rider.supportpages.domain.usecase

import com.mevron.rides.rider.supportpages.domain.repository.ISupportRepository
import javax.inject.Inject

class GetPromoUseCase @Inject constructor(private val repository: ISupportRepository) {
    suspend operator fun invoke() = repository.getPromos()
}