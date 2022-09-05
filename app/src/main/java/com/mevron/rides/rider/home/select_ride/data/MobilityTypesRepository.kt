package com.mevron.rides.rider.home.select_ride.data

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.home.select_ride.domain.IMobilityTypesRepository
import com.mevron.rides.rider.home.select_ride.domain.MobilityTypeDomainModel
import com.mevron.rides.rider.home.select_ride.domain.MobilityTypesRequest
import com.mevron.rides.rider.home.select_ride.model.Data
import com.mevron.rides.rider.home.select_ride.model.Fare2

class MobilityTypesRepository (
    private val api: MobilityTypesApi
) : IMobilityTypesRepository {

    override suspend fun getMobilityTypesData(request: MobilityTypesRequest): DomainModel =
        try {
            val response = api.getMobilityTypes(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    DomainModel.Success(data = it.success.data.map { data -> data.toDomainModel() })
                } ?: DomainModel.Success(data = listOf<MobilityTypeDomainModel>())
            } else {
                DomainModel.Error(Throwable(response.errorBody().toString()))
            }
        } catch (error: Throwable) {
            DomainModel.Error(error)
        }

    private fun Data.toDomainModel():MobilityTypeDomainModel{
        var values = mutableListOf<String>()
        for (v in this.fare2){
            if (v.name.lowercase() == ("Estimated Total").lowercase()){
               values = v.value.replace(" ", "").split("-").toMutableList()
              break
            }
        }

       return MobilityTypeDomainModel(
            currency = this.currency,
            dropOffTime = this.dropOffTime,
            fare = fare,
            id = id,
            image = image,
            name = name,
            seats = seats,
            type = type,
            minValue = values[0],
            maxValue = values[1]
        )
    }

}