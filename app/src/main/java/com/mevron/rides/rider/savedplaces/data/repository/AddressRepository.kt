package com.mevron.rides.rider.savedplaces.data.repository

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.remote.HTTPErrorHandler
import com.mevron.rides.rider.remote.model.GeneralResponse
import com.mevron.rides.rider.savedplaces.data.model.GetSavedAddress
import com.mevron.rides.rider.savedplaces.data.model.SaveAddressRequest
import com.mevron.rides.rider.savedplaces.data.model.UpdateAddress
import com.mevron.rides.rider.savedplaces.data.network.AddressAPI
import com.mevron.rides.rider.savedplaces.domain.model.AddressDomainData
import com.mevron.rides.rider.savedplaces.domain.model.GetAddressDomainData
import com.mevron.rides.rider.savedplaces.domain.model.GetSavedAddressData
import com.mevron.rides.rider.savedplaces.domain.repository.IAddressRepository
import com.mevron.rides.rider.util.Constants

class AddressRepository(private val addressAPI: AddressAPI) : IAddressRepository {

    override suspend fun getSavedAddresses(): DomainModel = addressAPI.getAddress().let {
        if (it.isSuccessful) {
            it.body()?.toDomainModel() ?: DomainModel.Error(Throwable(Constants.UNEXPECTED_ERROR))
        } else {
            val error = HTTPErrorHandler.handleErrorWithCode(it)
            DomainModel.Error(Throwable(error?.error?.message ?: Constants.UNEXPECTED_ERROR))
        }
    }

    override suspend fun addAnAddress(data: SaveAddressRequest): DomainModel =
        addressAPI.saveAddress(data).let {
            if (it.isSuccessful) {
                it.body()?.toDomainModel() ?: DomainModel.Error(Throwable(Constants.UNEXPECTED_ERROR))
            } else {
                val error = HTTPErrorHandler.handleErrorWithCode(it)
                DomainModel.Error(Throwable(error?.error?.message ?: Constants.UNEXPECTED_ERROR))
            }
        }

    override suspend fun updateAnAddress(identifier: String, data: UpdateAddress): DomainModel =
        addressAPI.updateAddress(identifier = identifier, data = data).let {
            if (it.isSuccessful) {
                it.body()?.toDomainModel() ?: DomainModel.Error(Throwable(Constants.UNEXPECTED_ERROR))
            } else {
                val error = HTTPErrorHandler.handleErrorWithCode(it)
                DomainModel.Error(Throwable(error?.error?.message ?: Constants.UNEXPECTED_ERROR))
            }
        }

    private fun GetSavedAddress.toDomainModel() = DomainModel.Success(
        data = GetAddressDomainData(savedAddresses = this.success.addData.map {
            GetSavedAddressData(
                latitude = it.latitude,
                longitude = it.longitude,
                type = it.type,
                address = it.address,
                uuid = it.uuid,
                name = it.name,
                lat = it.latitude,
                lng = it.longitude
            )
        })
    )

    private fun GeneralResponse.toDomainModel() = DomainModel.Success(
        data = AddressDomainData(message = this.success.message, status = this.success.status)
    )
}