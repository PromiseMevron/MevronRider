package com.mevron.rides.rider.settings.referal.domain.usecase

import com.mevron.rides.rider.settings.referal.data.model.SetReferal
import com.mevron.rides.rider.settings.referal.domain.repository.IReferalRepo
import javax.inject.Inject

class SetReferralUseCase @Inject constructor(private val repository: IReferalRepo) {
    suspend operator fun invoke(data: SetReferal) = repository.setReferral(data)
}