package com.mevron.rides.rider

import com.google.firebase.messaging.FirebaseMessagingService

class MevronFirebaseInstanceIDService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}