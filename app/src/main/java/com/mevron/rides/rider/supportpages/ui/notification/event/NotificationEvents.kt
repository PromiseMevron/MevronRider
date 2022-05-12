package com.mevron.rides.rider.supportpages.ui.notification.event

sealed interface NotificationEvents{
    object OnBackButtonClicked: NotificationEvents
    object GetNotifications: NotificationEvents
}