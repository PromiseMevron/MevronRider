package com.mevron.rides.rider.socket.domain.models

data class StateMachineSocketResponse(
    val current_state: String,
    val meta_data: MetaData
)