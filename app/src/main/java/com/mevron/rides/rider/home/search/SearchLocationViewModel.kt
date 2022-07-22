package com.mevron.rides.rider.home.search

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.usecase.SetOrderPropertiesUseCase
import com.mevron.rides.rider.home.model.LocationModel
import com.mevron.rides.rider.home.search.ui.SearchLocationEvent
import com.mevron.rides.rider.home.search.ui.SearchLocationState
import com.mevron.rides.rider.savedplaces.domain.model.GetAddressDomainData
import com.mevron.rides.rider.savedplaces.domain.usecase.GetAddressUseCase
import com.mevron.rides.rider.shared.ui.BaseViewModel
import com.mevron.rides.rider.util.Constants.DROP_OFF_ADD
import com.mevron.rides.rider.util.Constants.DROP_OFF_LAT
import com.mevron.rides.rider.util.Constants.DROP_OFF_LNG
import com.mevron.rides.rider.util.Constants.PICK_UP_ADD
import com.mevron.rides.rider.util.Constants.PICK_UP_LAT
import com.mevron.rides.rider.util.Constants.PICK_UP_LNG
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    private val getAddressUseCase: GetAddressUseCase,
    private val setOrderPropertiesUseCase: SetOrderPropertiesUseCase,
) : BaseViewModel<SearchLocationState, SearchLocationEvent>() {

    override fun createInitialState(): SearchLocationState = SearchLocationState.EMPTY

    private fun getAddresses() {
        setState { copy(isLoading = false, error = "") }

        CoroutineScope(Dispatchers.IO).launch {
            val result = getAddressUseCase()
            if (result is DomainModel.Success) {
                val data = result.data as GetAddressDomainData
                setState {
                    copy(
                        savedAddresses = data.savedAddresses,
                        isLoading = false,
                        error = ""
                    )
                }
            } else {
                setState { copy(error = (result as DomainModel.Error).error.toString()) }
            }
        }
    }

    private fun clearClickStateAndSetState(callback: () -> Unit) {
        setState {
            copy(
                isAddWorkClicked = false,
                isAddHomeClicked = false,
                isCurrentLocationClicked = false,
                isSetLocationOnTheMapClicked = false,
                error = ""
            )
        }

        callback()
    }

    override fun setEvent(event: SearchLocationEvent) = when (event) {
        SearchLocationEvent.AddHomeClicked -> clearClickStateAndSetState {
            setState { copy(isAddHomeClicked = true) }
        }
        SearchLocationEvent.AddWorkClicked -> clearClickStateAndSetState {
            setState { copy(isAddWorkClicked = true) }
        }
        SearchLocationEvent.CurrentLocationClicked -> clearClickStateAndSetState {
            setState { copy(isCurrentLocationClicked = true) }
        }
        SearchLocationEvent.SetLocationOnTheMapClicked -> clearClickStateAndSetState {
            setState { copy(isSetLocationOnTheMapClicked = true) }
        }
        SearchLocationEvent.GetAddress -> getAddresses()
    }

    fun clearState() {
        setState {
            copy(
                isAddWorkClicked = false,
                isAddHomeClicked = false,
                isCurrentLocationClicked = false,
                isSetLocationOnTheMapClicked = false,
                error = ""
            )
        }
    }

    fun updateOrderStatus(model: List<LocationModel>){
        setOrderPropertiesUseCase(PICK_UP_ADD, model[0].address)
        setOrderPropertiesUseCase(DROP_OFF_ADD, model[1].address)
        setOrderPropertiesUseCase(PICK_UP_LAT, model[0].lat.toString())
        setOrderPropertiesUseCase(DROP_OFF_LAT, model[1].lat.toString())
        setOrderPropertiesUseCase(PICK_UP_LNG, model[0].lng.toString())
        setOrderPropertiesUseCase(DROP_OFF_LNG, model[1].lng.toString())
    }
}