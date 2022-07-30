package com.mevron.rides.rider.home.booked

import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.TripState
import com.mevron.rides.rider.domain.usecase.GetTripStateUseCase
import com.mevron.rides.rider.home.booked.domain.BookedTripEvent
import com.mevron.rides.rider.home.booked.domain.BookedTripState
import com.mevron.rides.rider.home.booked.domain.UNDEFINED_COORDINATE
import com.mevron.rides.rider.home.booked.domain.toTripStatus
import com.mevron.rides.rider.shared.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookedViewModel @Inject constructor(
    private val getTripStateUseCase: GetTripStateUseCase
) : BaseViewModel<BookedTripState, BookedTripEvent>() {

    override fun createInitialState(): BookedTripState = BookedTripState.EMPTY

    private fun getTripState() {
        viewModelScope.launch {
            getTripStateUseCase().collect { tripState ->
            /*    if (tripState is TripState.TripStatusState) {
                    setState {
                        copy(
                            currentStatus = tripState.data.metaData.status.toTripStatus(),
                            pickupLatitude = tripState.data.metaData.trip.pickupLatitude.toNonNullDouble(),
                            pickupLongitude = tripState.data.metaData.trip.pickupLongitude.toNonNullDouble(),
                            dropOffLatitude = tripState.data.metaData.trip.destinationLatitude.toNonNullDouble(),
                            dropOffLongitude = tripState.data.metaData.trip.destinationLongitude.toNonNullDouble(),
                            pickupAddress = tripState.data.metaData.trip.pickupAddress,
                            destinationAddress = tripState.data.metaData.trip.destinationAddress
                        )
                    }
                }*/
                if (tripState is TripState.StateMachineState){
                    setState {
                        copy(
                            currentStatus = tripState.data.meta_data.status.toTripStatus(),
                            metaData = tripState.data.meta_data
                        )
                    }
                }

            }
        }
    }

    override fun setEvent(event: BookedTripEvent) {
        when (event) {
            BookedTripEvent.RequestTripStatus -> getTripState()
        }
    }
}

private fun String.toNonNullDouble(): Double = if (isEmpty()) UNDEFINED_COORDINATE else toDouble()