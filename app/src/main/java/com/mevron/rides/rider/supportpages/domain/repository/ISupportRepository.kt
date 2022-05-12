package com.mevron.rides.rider.supportpages.domain.repository

import com.mevron.rides.rider.domain.DomainModel

interface ISupportRepository {
    suspend fun getNotifications(): DomainModel
    suspend fun getPromos(): DomainModel
}