package com.mevron.rides.rider.settings.domain

import com.mevron.rides.rider.domain.DomainModel

interface ISettingsRepo {
    suspend fun signOut(): DomainModel
}