package com.mevron.rides.rider.home.data

import com.mevron.rides.rider.authentication.data.models.profile.ProfileData
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.home.domain.IProfileRepository
import com.mevron.rides.rider.home.domain.ProfileDomainData

class ProfileRepository(private val api: ProfileApi) : IProfileRepository {

    override suspend fun getProfile(): DomainModel =
        try {
            val result = api.getProfile()
            if (result.isSuccessful) {
                result.body()?.let {
                    it.success.profileData.toDomainModel()
                } ?: DomainModel.Error(Throwable("Error Loading profile data from api"))
            } else {
                DomainModel.Error(Throwable(result.errorBody().toString()))
            }
        } catch (error: Throwable) {
            DomainModel.Error(Throwable("Error connecting to network to fetch profile data"))
        }

   /* override suspend fun sendToken(id: DeviceID): DomainModel =
        try {
            val result = api.updateToken(id)
            if (result.isSuccessful) {
                DomainModel.Success(data = Unit)
            } else {
                DomainModel.Success(data = Unit)
            }
        } catch (error: Throwable) {
            DomainModel.Success(data = Unit)
        }*/

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
