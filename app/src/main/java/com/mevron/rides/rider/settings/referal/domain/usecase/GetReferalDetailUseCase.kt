package com.mevron.rides.rider.settings.referal.domain.usecase

import com.mevron.rides.rider.settings.referal.data.model.ReferalReport
import com.mevron.rides.rider.settings.referal.domain.repository.IReferalRepo
import javax.inject.Inject

class GetReferalDetailUseCase @Inject constructor(private val repository: IReferalRepo) {
    suspend operator fun invoke(data: ReferalReport) = repository.getAReferalFDetail(data)
}