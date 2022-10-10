package com.mevron.rides.rider.settings.data

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.remote.HTTPErrorHandler
import com.mevron.rides.rider.settings.domain.ISettingsRepo
import com.mevron.rides.rider.util.Constants

class SettingsRepo(private val api: SettingsAPI): ISettingsRepo{
    override suspend fun signOut(): DomainModel {
        return try {
            val response = api.signOut()
            if (response.isSuccessful) {
                DomainModel.Success(data = Unit)
            } else {

                val error = HTTPErrorHandler.handleErrorWithCode(response)
                DomainModel.Error(Throwable(error?.error?.message ?: Constants.UNEXPECTED_ERROR))
            }
        } catch (error: Throwable) {
            DomainModel.Error(Throwable(Constants.UNEXPECTED_ERROR))
        }
    }
}