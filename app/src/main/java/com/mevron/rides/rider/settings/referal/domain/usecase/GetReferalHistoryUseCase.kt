package com.mevron.rides.rider.settings.referal.domain.usecase

import com.mevron.rides.rider.settings.referal.domain.repository.IReferalRepo
import javax.inject.Inject


class GetReferalHistoryUseCase @Inject constructor(private val repository: IReferalRepo) {
    suspend operator fun invoke() = repository.getAllReferal()
}