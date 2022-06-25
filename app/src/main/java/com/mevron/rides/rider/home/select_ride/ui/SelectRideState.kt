package com.mevron.rides.rider.home.select_ride.ui

import com.mevron.rides.rider.home.model.GeoDirectionsResponse
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.home.select_ride.domain.MobilityTypeDomainModel

data class SelectRideState(
    val isLoading: Boolean,
    val mobilityTypes: List<MobilityTypeDomainModel>,
    val geoDirectionsResponse: GeoDirectionsResponse,
    val locationWrapper: LocationWrapper,
    val isMobilityTypesFetched: Boolean,
    val isOpenPaymentClicked: Boolean,
    val isListLoaded: Boolean,
    val isMarkerRendered: Boolean,
    val isLocationLoaded: Boolean,
    val error: String
) {
    val isMobilityTypeAvailable: Boolean
        get() = mobilityTypes.isNotEmpty()

    val isEmptyDirection: Boolean
        get() = geoDirectionsResponse.routes == null

    companion object {
        val EMPTY = SelectRideState(
            isLoading = false,
            mobilityTypes = listOf(),
            geoDirectionsResponse = GeoDirectionsResponse(null, null, null),
            locationWrapper = LocationWrapper.EMPTY,
            isMobilityTypesFetched = false,
            isOpenPaymentClicked = false,
            isListLoaded = false,
            isMarkerRendered = false,
            isLocationLoaded = false,
            error = "",
        )
    }
}

sealed interface SelectRideEvent {
    object RequestRideEvent : SelectRideEvent
    data class GeoResponseAvailable(val geoDirectionsResponse: GeoDirectionsResponse) : SelectRideEvent
    data class OnLocationAvailable(val location: LocationWrapper): SelectRideEvent
    object OpenPaymentFragmentEvent: SelectRideEvent
}

data class LocationWrapper(
    val model: Array<LocationModel>
) {
    companion object {
        val EMPTY = LocationWrapper(arrayOf())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocationWrapper

        if (!model.contentEquals(other.model)) return false

        return true
    }

    override fun hashCode(): Int {
        return model.contentHashCode()
    }
}