package com.mevron.rides.rider.home.booked

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.authentication.domain.model.VerifyOTPDomainModel
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.TripState
import com.mevron.rides.rider.domain.update
import com.mevron.rides.rider.domain.usecase.GetDriverLocationUseCase
import com.mevron.rides.rider.domain.usecase.GetOrderPropertiesUseCase
import com.mevron.rides.rider.domain.usecase.GetTripStateUseCase
import com.mevron.rides.rider.domain.usecase.SetOrderPropertiesUseCase
import com.mevron.rides.rider.emerg.data.model.UpdateEmergencyContact
import com.mevron.rides.rider.home.booked.domain.BookedTripEvent
import com.mevron.rides.rider.home.booked.domain.BookedTripState
import com.mevron.rides.rider.home.booked.domain.UNDEFINED_COORDINATE
import com.mevron.rides.rider.home.booked.domain.toTripStatus
import com.mevron.rides.rider.home.data.ShareTrip
import com.mevron.rides.rider.home.model.DriverLocationModel
import com.mevron.rides.rider.home.ride.domain.CancelRideRequestUseCase
import com.mevron.rides.rider.payment.data.RateDriverRequest
import com.mevron.rides.rider.payment.domain.RateRiderUseCase
import com.mevron.rides.rider.payment.domain.SendTipAndReviewUseCase
import com.mevron.rides.rider.payment.domain.TipAndReviewData
import com.mevron.rides.rider.remote.GenericStatus
import com.mevron.rides.rider.remote.HTTPErrorHandler
import com.mevron.rides.rider.remote.MevronRepo
import com.mevron.rides.rider.remote.model.CancelRideRequest
import com.mevron.rides.rider.remote.model.GeneralResponse
import com.mevron.rides.rider.shared.ui.BaseViewModel
import com.mevron.rides.rider.sharedprefrence.domain.usescases.GetPreferenceUseCase
import com.mevron.rides.rider.util.Constants
import com.mevron.rides.rider.util.Constants.TRIP_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
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
    private val rateRider: RateRiderUseCase,
    private val getOrderPropertiesUseCase: GetOrderPropertiesUseCase,
    private val getDriverLocationUseCase: GetDriverLocationUseCase,
    private val cancelRideRequestUseCase: CancelRideRequestUseCase,
    private val repo: MevronRepo,
    private val setPrefrence: SetOrderPropertiesUseCase
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
                    setPrefrence(TRIP_ID, tripState.data.meta_data.trip.tripId)
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

    fun getLocationModels(){
        setState {
            copy(
              ////  pickupAddress = getOrderPropertiesUseCase(Constants.PICK_UP_ADD),
               // destinationAddress = getOrderPropertiesUseCase(Constants.DROP_OFF_ADD),
               // pickupLatitude = getOrderPropertiesUseCase(Constants.PICK_UP_LAT).toDouble(),
              //  pickupLongitude = getOrderPropertiesUseCase(Constants.PICK_UP_LNG).toDouble(),
              //  dropOffLatitude = getOrderPropertiesUseCase(Constants.DROP_OFF_LAT).toDouble(),
               // dropOffLongitude = getOrderPropertiesUseCase(Constants.DROP_OFF_LNG).toDouble(),
            )
        }
    }

    fun cancelARide(){
        val request = CancelRideRequest(trip_id = uiState.value.metaData?.trip?.tripId ?: getOrderPropertiesUseCase(
            TRIP_ID), comment = uiState.value.reasonForCancel)
        viewModelScope.launch {
            val result = cancelRideRequestUseCase(request)

            if (result is DomainModel.Success) {
                setState {
                    copy(
                        isRideCancelled = true
                    )
                }
            }else{
                setState {
                    copy(
                        isRideCancelled = false
                    )
                }
            }
        }
    }

    fun updateCancelValue(value: String) {
        setState {
            copy(reasonForCancel = value)
        }
    }

    fun updateTipValue(amount: Int) {
        setState {
            copy(
                tipRider = amount
            )
        }
    }
    fun markerAdded() {
        setState { copy(isMarkerRendered = true) }
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

    fun getDriverLocation(){
        viewModelScope.launch {
            getDriverLocationUseCase().collect { tripState ->
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
                if (tripState != DriverLocationModel.EMPTY) {
                    setState {
                        copy(
                            driverLocation =tripState
                        )
                    }
                }
            }
        }
    }

    fun shareTrip(
        data: ShareTrip
    ): LiveData<GenericStatus<GeneralResponse>> {

        val result = MutableLiveData<GenericStatus<GeneralResponse>>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repo.shareTripDetails(data = data)
                if (response.isSuccessful) {
                    result.postValue(GenericStatus.Success(response.body()))
                } else {
                    result.postValue(
                        GenericStatus.Error(
                            HTTPErrorHandler.handleErrorWithCode(
                                response
                            )
                        )
                    )
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                result.postValue(GenericStatus.Error(HTTPErrorHandler.httpFailWithCode(ex)))
            }
        }
        return result
    }

    override fun setEvent(event: BookedTripEvent) {
        when (event) {
            BookedTripEvent.RequestTripStatus -> getTripState()
        }
    }
}

private fun String.toNonNullDouble(): Double = if (isEmpty()) UNDEFINED_COORDINATE else toDouble()