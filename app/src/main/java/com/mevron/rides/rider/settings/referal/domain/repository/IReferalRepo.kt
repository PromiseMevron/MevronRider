package com.mevron.rides.rider.settings.referal.domain.repository

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.settings.referal.data.model.ReferalReport
import com.mevron.rides.rider.settings.referal.data.model.SetReferal

interface IReferalRepo {
    suspend fun getAllReferal(): DomainModel
    suspend fun getAReferalFDetail(data: ReferalReport): DomainModel
    suspend fun setReferral(data: SetReferal): DomainModel
}