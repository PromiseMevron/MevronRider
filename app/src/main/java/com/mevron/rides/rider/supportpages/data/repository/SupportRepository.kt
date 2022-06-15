package com.mevron.rides.rider.supportpages.data.repository

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.supportpages.data.model.NotificationResponse
import com.mevron.rides.rider.supportpages.data.model.PromoResponse
import com.mevron.rides.rider.supportpages.data.network.SupportAPI
import com.mevron.rides.rider.supportpages.domain.model.SupportDomainData
import com.mevron.rides.rider.supportpages.domain.model.Supports
import com.mevron.rides.rider.supportpages.domain.repository.ISupportRepository

class SupportRepository (private val api: SupportAPI): ISupportRepository {

    override suspend fun getNotifications() = api.getNotifications().let {
        if (it.isSuccessful) {
            it.body()?.toDomainModel() ?: DomainModel.Error(Throwable("Empty result found"))
        } else {
            DomainModel.Error(Throwable(it.errorBody().toString()))
        }
    }

    override suspend fun getPromos() = api.getPromo().let {
        if (it.isSuccessful) {
            it.body()?.toDomainModel() ?: DomainModel.Error(Throwable("Empty result found"))
        } else {
            DomainModel.Error(Throwable(it.errorBody().toString()))
        }
    }

    private fun NotificationResponse.toDomainModel() = DomainModel.Success(
        data = SupportDomainData(
            notifications = this.success.notificationData.result.map {
                Supports(heading = it.title, subHeading = it.description)
            }
        )
    )

    private fun PromoResponse.toDomainModel() = DomainModel.Success(
        data = SupportDomainData(
            notifications = this.success.promoData.map {
                Supports(heading = it.description, subHeading = it.remain)
            }
        )
    )
}