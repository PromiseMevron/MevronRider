package com.mevron.rides.rider.settings.referal.ui.event

sealed interface ReferalEvent{
    object GetReferalDetail: ReferalEvent
    object SetReferalDetail: ReferalEvent
    object GetReferalPrefDetail: ReferalEvent

}