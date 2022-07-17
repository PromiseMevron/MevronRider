package com.mevron.rides.rider.payment.tipping

import com.mevron.rides.rider.shared.ui.SingleStateEvent

data class AddTipViewState(
    val rating: Int,
    val comments: List<Comment>,
    val tip: TipPercent,
    val isLoading: Boolean,
    val isShowRawTipClicked: SingleStateEvent<Unit>,
    val shouldNavigateToHome: SingleStateEvent<Unit>,
    val error: String
) {

    fun hasComment(comment: Comment) = comments.contains(comment)

    companion object {
        val EMPTY = AddTipViewState(
            rating = 0,
            comments = listOf(),
            tip = TipPercent.Nothing,
            isLoading = false,
            isShowRawTipClicked = SingleStateEvent(),
            shouldNavigateToHome = SingleStateEvent(),
            error = ""
        )
    }
}

sealed interface AddTipViewEvent {
    object AddTipClicked : AddTipViewEvent
    data class CommentAdded(val comment: Comment) : AddTipViewEvent
    data class TipAdded(val tip: TipPercent) : AddTipViewEvent
    data class RatingAdded(val rating: Int) : AddTipViewEvent
    object ShowRawTipDialog : AddTipViewEvent
    object IncreaseTipClicked : AddTipViewEvent
    object DecreaseTipClicked : AddTipViewEvent
}

enum class Comment(value: String) {
    CLEANLINESS("cleanliness"),
    NAVIGATION("navigation"),
    PRICE("price"),
    SERVICE("service"),
    ROUTE("route"),
    DRIVING("driving"),
    OTHER("other")
}

sealed class TipPercent(val value: Int) {
    object Nothing : TipPercent(0)
    object FourPercent : TipPercent(4)
    object SevenPercent : TipPercent(7)
    object TenPercent : TipPercent(10)
    data class RawTip(val amount: Int) : TipPercent(amount)
}