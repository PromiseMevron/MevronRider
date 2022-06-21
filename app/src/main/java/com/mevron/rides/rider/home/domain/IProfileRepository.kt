package com.mevron.rides.rider.home.domain

import com.mevron.rides.rider.domain.DomainModel

interface IProfileRepository {
    suspend fun getProfile(): DomainModel
}