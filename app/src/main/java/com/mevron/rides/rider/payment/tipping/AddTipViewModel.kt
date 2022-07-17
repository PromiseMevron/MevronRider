package com.mevron.rides.rider.payment.tipping

import androidx.lifecycle.viewModelScope
import com.mevron.rides.rider.domain.DomainModel
import com.mevron.rides.rider.domain.TripState
import com.mevron.rides.rider.domain.usecase.GetTripStateUseCase
import com.mevron.rides.rider.domain.usecase.SetTripStateUseCase
import com.mevron.rides.rider.payment.domain.SendTipAndReviewUseCase
import com.mevron.rides.rider.payment.domain.TipAndReviewData
import com.mevron.rides.rider.shared.ui.BaseViewModel
import com.mevron.rides.rider.shared.ui.SingleStateEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class AddTipViewModel @Inject constructor(
    private val addTipAndReviewUseCase: SendTipAndReviewUseCase,
    private val getTripStateUseCase: GetTripStateUseCase,
    private val setTripStateUseCase: SetTripStateUseCase
) : BaseViewModel<AddTipViewState, AddTipViewEvent>() {

    override fun createInitialState(): AddTipViewState = AddTipViewState.EMPTY

    private fun sendTipEvent() {
        setState { copy(isLoading = true) }
        val currentTripState = getTripStateUseCase().value
        if (currentTripState !is TripState.TripStatusState) {
            setState { copy(error = "Failed to loading trip data") }
            return
        }

        val request = uiState.value.toTipAndReviewData(currentTripState.data.metaData.trip.tripId)
        viewModelScope.launch(Dispatchers.IO) {
            val response = addTipAndReviewUseCase(request)
            if (response is DomainModel.Success) {
                setTripStateUseCase(TripState.Idle)
            } else {
                setState {
                    copy(
                        isLoading = false,
                        error = (response as DomainModel.Error).error.message.toString()
                    )
                }
            }
        }
    }

    private fun toggleComment(comment: Comment) {
        val comments = uiState.value.comments.toMutableList()
        if (comments.contains(comment)) {
            comments.remove(comment)
        } else {
            comments.add(comment)
        }

        setState { copy(comments = comments, error = "") }
    }

    private fun updateTipValue(tipPercent: TipPercent) = setState { copy(tip = tipPercent, error = "") }

    override fun setEvent(event: AddTipViewEvent) {
        when (event) {
            AddTipViewEvent.AddTipClicked -> sendTipEvent()
            is AddTipViewEvent.CommentAdded -> toggleComment(event.comment)
            is AddTipViewEvent.RatingAdded -> setState { copy(rating = event.rating, error = "") }
            is AddTipViewEvent.TipAdded -> updateTipValue(event.tip)
            AddTipViewEvent.ShowRawTipDialog ->
                setState {
                    copy(isShowRawTipClicked = SingleStateEvent<Unit>().apply { set(Unit) }, error = "")
                }
            AddTipViewEvent.DecreaseTipClicked -> decreaseTip()
            AddTipViewEvent.IncreaseTipClicked -> increaseTip()
        }
    }

    private fun decreaseTip() {
        val currentTip =
            if (uiState.value.tip is TipPercent.RawTip) (uiState.value.tip as TipPercent.RawTip).amount
            else 0
        if (currentTip == 0) return
        setState { copy(tip = TipPercent.RawTip(amount = currentTip + 1), error = "") }
    }

    private fun increaseTip() {
        val currentTip =
            if (uiState.value.tip is TipPercent.RawTip) (uiState.value.tip as TipPercent.RawTip).amount
            else 0
        setState { copy(tip = TipPercent.RawTip(amount = currentTip + 1), error = "") }
    }

    private fun AddTipViewState.toTipAndReviewData(tripId: String): TipAndReviewData =
        TipAndReviewData(
            tripId = tripId,
            tipAmount = tip.value,
            comment = comments.map { it.name }
        )

    fun getTipValue(): Int {
        return when (val currentTip = uiState.value.tip) {
            is TipPercent.RawTip -> currentTip.amount
            else -> currentTip.value
        }
    }
}