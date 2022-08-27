package com.mevron.rides.rider.home.booked

import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.authentication.domain.model.VerifyOTPDomainModel
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.TripState
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.domain.usecase.GetTripStateUseCase
import com.mevron.rides.rider.home.booked.domain.BookedTripEvent
import com.mevron.rides.rider.home.booked.domain.BookedTripState
import com.mevron.rides.rider.home.booked.domain.UNDEFINED_COORDINATE
import com.mevron.rides.rider.home.booked.domain.toTripStatus
import com.mevron.rides.rider.payment.data.RateDriverRequest
import com.mevron.rides.rider.payment.domain.RateRiderUseCase
import com.mevron.rides.rider.payment.domain.SendTipAndReviewUseCase
import com.mevron.rides.rider.payment.domain.TipAndReviewData
import com.mevron.rides.rider.shared.ui.BaseViewModel
import com.mevron.rides.rider.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.internal.userAgent
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BookedViewModel @Inject constructor(
    private val getTripStateUseCase: GetTripStateUseCase,
    private val tipReview: SendTipAndReviewUseCase,
    private val rateRider: RateRiderUseCase
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
                if (tripState is TripState.StateMachineState) {
                    setState {
                        copy(
                            currentStatus = tripState.data.meta_data.status.toTripStatus(),
                            metaData = tripState.data.meta_data,
                          //  pickupLatitude = tripState.data.meta_data.trip.pickupLatitude.toNonNullDouble(),
                          // pickupLongitude = tripState.data.meta_data.trip.pickupLongitude.toNonNullDouble(),
                           // dropOffLatitude = tripState.data.meta_data.trip.destinationLatitude.toNonNullDouble(),
                           // dropOffLongitude = tripState.data.meta_data.trip.destinationLongitude.toNonNullDouble(),

                            )
                    }
                }
            }
        }
    }

    fun updateTipValue(amount: Int) {
        setState {
            copy(
                tipRider = amount
            )
        }
    }

    fun updateRateComment(customerComment: String) {
        val comments = uiState.value.review
        if (comments.contains(customerComment)) {
            val index = comments.indexOf(customerComment)
            comments.removeAt(index)
        } else {
            comments.add(customerComment)
        }

        setState {
            copy(
                review = comments
            )
        }
    }

    fun updateRating(rating: String){
        setState {
            copy(
                customRating = rating
            )
        }
    }

    fun tipAndRateCustom() {
        val currentValues = uiState.value
        val data = TipAndReviewData(
            comment = currentValues.review,
            tripId = currentValues.metaData?.trip?.tripId ?: "",
            tipAmount = currentValues.tipRider
        )

        viewModelScope.launch(Dispatchers.IO) {
            setState {
                copy(
                    loading = true
                )
            }
            when (tipReview(data = data)) {
                is DomainModel.Success -> {
                    setState {
                        copy(
                            isSuccess = true,
                            loading = false
                        )
                    }
                }
                is DomainModel.Error -> {
                    setState {
                        copy(
                            isSuccess = false,
                            error = "Error in reviewing and tipping driver",
                            loading = false
                        )
                    }
                }
            }
        }
    }

    fun rateCustom(rate: Float) {
        val currentValues = uiState.value
        val data = RateDriverRequest(
            tripId = currentValues.metaData?.trip?.tripId ?: "",
            rating = rate.toInt()
        )

        viewModelScope.launch(Dispatchers.IO) {
            setState {
                copy(
                    loading = false
                )
            }
            when (rateRider(data = data)) {
                is DomainModel.Success -> {
                    setState {
                        copy(
                          //  loading = false
                        )
                    }
                }
                is DomainModel.Error -> {
                    setState {
                        copy(
                          //  error = "Error in rating driver",
                          //  loading = false
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