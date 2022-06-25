package com.mevron.rides.rider.home.search

import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.home.search.ui.SearchLocationEvent
import com.mevron.rides.rider.home.search.ui.SearchLocationState
import com.mevron.rides.rider.savedplaces.domain.model.GetAddressDomainData
import com.mevron.rides.rider.savedplaces.domain.usecase.GetAddressUseCase
import com.mevron.rides.rider.shared.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    private val getAddressUseCase: GetAddressUseCase
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
}