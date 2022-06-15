package com.mevron.rides.rider.supportpages.ui.promo.event

sealed interface PromoEvents{
    object OnBackButtonClicked: PromoEvents
    object GetPromotions: PromoEvents
}