package com.mevron.rides.rider.home.data

import com.mevron.rides.rider.authentication.data.models.profile.ProfileData
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.home.domain.IProfileRepository
import com.mevron.rides.rider.home.domain.ProfileDomainData
import com.mevron.rides.rider.remote.HTTPErrorHandler
import com.mevron.rides.rider.util.Constants

class ProfileRepository(private val api: ProfileApi) : IProfileRepository {

    override suspend fun getProfile(): DomainModel =
        try {
            val result = api.getProfile()
            if (result.isSuccessful) {
                result.body()?.let {
                    it.success.profileData.toDomainModel()
                } ?: DomainModel.Error(Throwable(Constants.UNEXPECTED_ERROR))
            } else {
                val error = HTTPErrorHandler.handleErrorWithCode(result)
                DomainModel.Error(Throwable(error?.error?.message ?: Constants.UNEXPECTED_ERROR))
            }
        } catch (error: Throwable) {
            DomainModel.Error(Throwable(Constants.UNEXPECTED_ERROR))
        }

    override suspend fun sendToken(id: DeviceID): DomainModel =
        try {
            val result = api.postToken(id)
            if (result.isSuccessful) {
                DomainModel.Success(data = Unit)
            } else {
                DomainModel.Success(data = Unit)
            }
        } catch (error: Throwable) {
            DomainModel.Success(data = Unit)
        }

}

private fun ProfileData.toDomainModel(): DomainModel.Success =
    DomainModel.Success(
        data = ProfileDomainData(
            email = this.email ?: "",
            emailStatus = this.emailStatus,
            firstName = this.firstName ?: "",
            lastName = this.lastName ?: "",
            phoneNumber = this.phoneNumber ?: "",
            phoneNumberStatus = this.phoneNumberStatus,
            profilePicture = this.profilePicture,
            rating = this.rating,
            uuid = this.uuid,
            supportNumber = this.supportNumber
        )
    )
