package com.mevron.rides.rider.settings.referal.data.repository

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.settings.referal.data.model.ReferalReport
import com.mevron.rides.rider.settings.referal.data.model.SetReferal
import com.mevron.rides.rider.settings.referal.data.network.ReferralApi
import com.mevron.rides.rider.settings.referal.domain.model.ReferalNumber
import com.mevron.rides.rider.settings.referal.domain.model.ReferralData
import com.mevron.rides.rider.settings.referal.domain.model.ReferralHistory
import com.mevron.rides.rider.settings.referal.domain.repository.IReferalRepo

class ReferralRepo(private val api: ReferralApi) : IReferalRepo {
    override suspend fun getAllReferal(): DomainModel {
        return try {
            val response = api.getReferralHistory()
            if (response.isSuccessful) {
                val data = response.body()?.success?.data?.result?.map {
                    ReferralData(it.category, it.createAt, it.description, it.id, it.title)
                } ?: mutableListOf()
                val dt = response.body()?.success?.data
                DomainModel.Success(
                    data = ReferralHistory(
                        referralData = data,
                        referralCode = dt?.referralCode,
                        referralStatus = dt?.referralStatus ?: 0
                    )
                )
            } else {
                DomainModel.Error(Throwable(response.errorBody().toString()))
            }
        } catch (error: Throwable) {
            DomainModel.Error(Throwable("Error setting referral"))
        }
    }

    override suspend fun getAReferalFDetail(data: ReferalReport): DomainModel {
        return try {
            val response = api.getReferralReport(data)
            if (response.isSuccessful) {
                val numbers = response.body()?.success?.refData?.rides ?: "0"
                DomainModel.Success(data = ReferalNumber(rides = numbers))
            } else {
                DomainModel.Error(Throwable(response.errorBody().toString()))
            }
        } catch (error: Throwable) {
            DomainModel.Error(Throwable("Error setting referral"))
        }
    }

    override suspend fun setReferral(data: SetReferal): DomainModel {
        return try {
            val response = api.setReferral(data)
            if (response.isSuccessful) {
                DomainModel.Success(data = Unit)
            } else {
                DomainModel.Error(Throwable(response.errorBody().toString()))
            }
        } catch (error: Throwable) {
            DomainModel.Error(Throwable("Error setting referral"))
        }
    }
}