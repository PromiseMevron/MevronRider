package com.mevron.rides.rider.settings.data

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.settings.domain.ISettingsRepo

class SettingsRepo(private val api: SettingsAPI): ISettingsRepo{
    override suspend fun signOut(): DomainModel {
        return try {
            val response = api.signOut()
            if (response.isSuccessful) {
                DomainModel.Success(data = Unit)
            } else {
                DomainModel.Error(Throwable(response.errorBody().toString()))
            }
        } catch (error: Throwable) {
            DomainModel.Error(Throwable("Error in signing out"))
        }
    }
}