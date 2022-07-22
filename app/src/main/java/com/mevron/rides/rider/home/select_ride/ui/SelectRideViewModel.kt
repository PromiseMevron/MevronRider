package com.mevron.rides.rider.home.select_ride.ui

import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.usecase.SetOrderPropertiesUseCase
import com.mevron.rides.rider.home.model.GeoDirectionsResponse
import com.mevron.rides.rider.home.select_ride.domain.GetMobilityTypesUseCase
import com.mevron.rides.rider.home.select_ride.domain.MobilityTypeDomainModel
import com.mevron.rides.rider.home.select_ride.domain.MobilityTypesRequest
import com.mevron.rides.rider.shared.ui.BaseViewModel
import com.mevron.rides.rider.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class SelectRideViewModel @Inject constructor(
    private val getMobilityTypesUseCase: GetMobilityTypesUseCase,
    private val setOrderPropertiesUseCase: SetOrderPropertiesUseCase
) : BaseViewModel<SelectRideState, SelectRideEvent>() {

    override fun createInitialState(): SelectRideState = SelectRideState.EMPTY

    private fun loadMobilityTypes() {
        val location = uiState.value.locationWrapper.model
        val request = MobilityTypesRequest(
            destinationAddress = location[1].address,
            destinationLatitude = location[1].lat.toString(),
            destinationLongitude = location[1].lng.toString(),
            pickupAddress = location[0].address,
            pickupLatitude = location[0].lat.toString(),
            pickupLongitude = location[0].lng.toString()
        )
        setState { copy(isLoading = true, error = "") }
        viewModelScope.launch(Dispatchers.IO) {
            val domainModel = getMobilityTypesUseCase(request)
            if (domainModel is DomainModel.Success) {
                val data = domainModel.data as List<MobilityTypeDomainModel>
                setState { copy(isLoading = false, mobilityTypes = data, isMobilityTypesFetched = true) }
            } else {
                setState {
                    copy(
                        isLoading = false,
                        isMobilityTypesFetched = true,
                        error = (domainModel as DomainModel.Error).error.toString()
                    )
                }
            }
        }
    }

    fun resetDirectionResponse() {
        setState { copy(geoDirectionsResponse = GeoDirectionsResponse(null, null, null)) }
    }

    fun setUpPaymentMethod(id: String){
        setOrderPropertiesUseCase(Constants.CAR, id)
    }

    override fun setEvent(event: SelectRideEvent) {
        when (event) {
            is SelectRideEvent.RequestRideEvent -> loadMobilityTypes()
            is SelectRideEvent.GeoResponseAvailable -> if (uiState.value.isEmptyDirection) {
                setState {
                    copy(geoDirectionsResponse = event.geoDirectionsResponse)
                }
            }
            is SelectRideEvent.OnLocationAvailable -> {
                if (event.location.model.isNotEmpty()) {
                    setState {
                        copy(locationWrapper = event.location)
                    }
                }
            }
            SelectRideEvent.OpenPaymentFragmentEvent -> setState { copy(isOpenPaymentClicked = true) }
        }
    }
}